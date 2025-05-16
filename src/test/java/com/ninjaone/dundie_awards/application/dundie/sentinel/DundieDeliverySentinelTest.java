package com.ninjaone.dundie_awards.application.dundie.sentinel;

import com.ninjaone.dundie_awards.application.dundie.publisher.DundieDeliverPublisher;
import com.ninjaone.dundie_awards.infrastructure.helper.RabbitHealthChecker;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunk;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkRepository;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkStatus;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDelivery;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryRepository;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkStatus.PENDING;
import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkStatus.PENDING_ROLLBACK;
import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryStatusEnum.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class DundieDeliverySentinelTest {

    @InjectMocks
    private DundieDeliverySentinel sentinel;

    @Mock
    private RabbitHealthChecker rabbitHealthChecker;

    @Mock
    private DundieDeliveryRepository deliveryRepository;

    @Mock
    private DundieDeliveryChunkRepository chunkRepository;

    @Mock
    private DundieDeliverPublisher publisher;

    @BeforeEach
    void setUp() {
        openMocks(this);
        setField(sentinel, "quantityToSearch", 10);
        setField(sentinel, "delayedMinutes", 5);
    }

    @Test
    void shouldRestartPendingDeliveriesBasedOnStatus() {
        when(rabbitHealthChecker.isOnline()).thenReturn(true);

        var pendingDeliveries = List.of(
            mockDelivery(1L, PENDING_SPLIT),
            mockDelivery(2L, DELIVERED),
            mockDelivery(3L, ERROR_ON_ACTIVITY)
        );
        when(deliveryRepository.findTopByStatusWithDelay(10, 5, PENDING_SPLIT, DELIVERED, ERROR_ON_ACTIVITY)).thenReturn(pendingDeliveries);

        sentinel.restartPendingDeliveries();

        verify(publisher).toDundieDeliverySplitQueue(1L);
        verify(publisher).toActivityQueue(2L);
        verify(publisher).toDundieDeliverySplitRollbackQueue(3L);
    }

    @Test
    void shouldNotDoAnythingIfRabbitIsOffline() {
        when(rabbitHealthChecker.isOnline()).thenReturn(false);

        sentinel.restartPendingDeliveries();
        sentinel.restartPendingDeliveryChunks();

        verifyNoInteractions(deliveryRepository, chunkRepository, publisher);
    }

    @Test
    void shouldRestartPendingChunksBasedOnStatus() {
        when(rabbitHealthChecker.isOnline()).thenReturn(true);

        var chunks = List.of(
            mockChunk(100L, PENDING),
            mockChunk(200L, PENDING_ROLLBACK)
        );

        when(chunkRepository.findTopPendingChunksWithDelay(10, 5, PENDING, PENDING_ROLLBACK)).thenReturn(chunks);

        sentinel.restartPendingDeliveryChunks();

        verify(publisher).toDundieDeliveryQueue(100L);
        verify(publisher).toDundieDeliveryRollbackQueue(200L);
    }

    private DundieDelivery mockDelivery(Long id, DundieDeliveryStatusEnum status) {
        DundieDelivery delivery = mock(DundieDelivery.class);
        when(delivery.getId()).thenReturn(id);
        when(delivery.getStatus()).thenReturn(status);

        return delivery;
    }

    private DundieDeliveryChunk mockChunk(Long id, DundieDeliveryChunkStatus status) {
        DundieDeliveryChunk chunk = mock(DundieDeliveryChunk.class);
        when(chunk.getId()).thenReturn(id);
        when(chunk.getStatus()).thenReturn(status);

        return chunk;
    }
}
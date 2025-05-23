package com.ninjaone.dundie_awards.application.dundie.split;

import com.ninjaone.dundie_awards.application.dundie.publisher.DundieDeliverPublisher;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkRepository;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDelivery;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkStatus.PENDING_ROLLBACK;
import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryStatusEnum.ERROR_ON_ACTIVITY;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class DundieDeliverySplitRollbackConsumerTest {

    @InjectMocks
    private DundieDeliverySplitRollbackConsumer consumer;

    @Mock
    private DundieDeliveryRepository dundieDeliveryRepository;

    @Mock
    private DundieDeliveryChunkRepository chunkRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private DundieDeliverPublisher publisher;

    @Mock
    private DundieDelivery dundieDelivery;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void shouldProcessRollbackAndPublishEvent() {
        Long deliveryId = 1L;

        when(dundieDeliveryRepository.findByIdAndStatusWithLock(deliveryId, ERROR_ON_ACTIVITY)).thenReturn(Optional.of(dundieDelivery));

        List<Long> chunkIds = List.of(101L, 102L);
        when(chunkRepository.findIdsByDundieDeliveryIdAndStatus(deliveryId, PENDING_ROLLBACK)).thenReturn(chunkIds);

        consumer.rollback(deliveryId);

        verify(dundieDeliveryRepository).toRunning(dundieDelivery);
        verify(chunkRepository).createRollbackChunksToDelivery(deliveryId);
        verify(eventPublisher).publishEvent(argThat(event -> ((DundieDeliverySplitRollbackFinished) event).getChunksIds().equals(chunkIds)));
    }

    @Test
    void shouldSendChunksToRollbackQueueWhenEventIsPublished() {
        List<Long> chunkIds = List.of(101L, 102L);
        DundieDeliverySplitRollbackFinished event = new DundieDeliverySplitRollbackFinished(this, chunkIds);

        consumer.onDundieDeliverySplitRollbackFinished(event);

        verify(publisher).toDundieDeliveryRollbackQueue(101L);
        verify(publisher).toDundieDeliveryRollbackQueue(102L);
    }

}
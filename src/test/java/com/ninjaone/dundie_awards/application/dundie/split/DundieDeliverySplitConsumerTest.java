package com.ninjaone.dundie_awards.application.dundie.split;

import com.ninjaone.dundie_awards.application.dundie.publisher.DundieDeliverPublisher;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkRepository;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDelivery;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryRepository;
import com.ninjaone.dundie_awards.infrastructure.repository.employee.EmployeeIds;
import com.ninjaone.dundie_awards.infrastructure.repository.employee.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryStatusEnum.PENDING_SPLIT;
import static java.util.Optional.of;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class DundieDeliverySplitConsumerTest {

    @InjectMocks
    private DundieDeliverySplitConsumer consumer;

    @Mock
    private DundieDeliveryRepository dundieDeliveryRepository;

    @Mock
    private DundieDeliveryChunkRepository chunkRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private DundieDeliverPublisher publisher;

    @Mock
    private DundieDelivery dundieDelivery;

    @Mock
    private EmployeeIds employeeIdsOne;

    @Mock
    private EmployeeIds employeeIdsTwo;

    @BeforeEach
    void setUp() {
        openMocks(this);

        when(employeeIdsOne.getStartEmployeeId()).thenReturn(1L);
        when(employeeIdsOne.getEndEmployeeId()).thenReturn(10L);

        when(employeeIdsTwo.getStartEmployeeId()).thenReturn(11L);
        when(employeeIdsTwo.getEndEmployeeId()).thenReturn(20L);
    }

    @Test
    void shouldProcessSplitAndPublishEvent() {
        Long deliveryId = 1L;

        long organizationId = 100L;
        when(dundieDelivery.getOrganizationId()).thenReturn(organizationId);

        when(dundieDeliveryRepository.findByIdAndStatusWithLock(deliveryId, PENDING_SPLIT)).thenReturn(of(dundieDelivery));

        List<EmployeeIds> employeeIds = List.of(employeeIdsOne, employeeIdsTwo);
        when(employeeRepository.findChunksOfEmployees(organizationId)).thenReturn(employeeIds);

        List<Long> chunkIds = List.of(100L, 200L);
        when(chunkRepository.createChunks(dundieDelivery, employeeIds)).thenReturn(chunkIds);

        consumer.receive(deliveryId);

        verify(dundieDeliveryRepository).toRunning(dundieDelivery);
        verify(eventPublisher).publishEvent(argThat(event -> ((DundieDeliverySplitFinished) event).getChunksIds().equals(chunkIds)));
    }

    @Test
    void shouldSendChunksToQueueWhenEventIsPublished() {
        List<Long> chunkIds = List.of(10L, 20L);
        DundieDeliverySplitFinished event = new DundieDeliverySplitFinished(this, chunkIds);

        consumer.onDundieDeliverySplitFinished(event);

        verify(publisher).toDundieDeliveryQueue(10L);
        verify(publisher).toDundieDeliveryQueue(20L);
    }
}
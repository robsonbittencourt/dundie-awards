package com.ninjaone.dundie_awards.application.activity;

import com.ninjaone.dundie_awards.application.dundie.publisher.DundieDeliverPublisher;
import com.ninjaone.dundie_awards.infrastructure.helper.TransactionHelper;
import com.ninjaone.dundie_awards.infrastructure.repository.activity.ActivityRepository;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDelivery;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryStatusEnum.DELIVERED;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class ActivityConsumerTest {

    @InjectMocks
    private ActivityConsumer consumer;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private DundieDeliveryRepository dundieDeliveryRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private DundieDeliverPublisher publisher;

    @Mock
    private DundieDelivery dundieDelivery;

    @Mock
    private TransactionHelper transactionHelper;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void shouldSaveActivityAndSetFinished() {
        Long deliveryId = 123L;
        when(dundieDeliveryRepository.findByIdAndStatusWithLock(deliveryId, DELIVERED)).thenReturn(Optional.of(dundieDelivery));
        when(dundieDelivery.getOrganizationId()).thenReturn(99L);

        consumer.createActivity(deliveryId);

        verify(activityRepository).save(argThat(activity ->
            activity.getEvent().equals("Dundie was delivered to organization 99") && activity.getOccuredAt() != null
        ));
        verify(dundieDeliveryRepository).toFinished(dundieDelivery);
        verifyNoInteractions(eventPublisher, publisher);
    }

    @Test
    void shouldRollbackAndPublishEvent() {
        Long deliveryId = 123L;

        when(dundieDeliveryRepository.findByIdAndStatusWithLock(deliveryId, DELIVERED)).thenThrow(new RuntimeException("DB error"));

        consumer.createActivity(deliveryId);

        verify(eventPublisher).publishEvent(argThat(event -> ((ActivityCreationFailed) event).getDundieDeliveryId().equals(deliveryId)));
        verify(dundieDeliveryRepository, never()).toFinished(any());
    }

    @Test
    void shouldMarkErrorAndPublishRollback() {
        Long deliveryId = 123L;
        ActivityCreationFailed event = new ActivityCreationFailed(this, deliveryId);

        consumer.onDundieDeliverySplitFinished(event);

        verify(dundieDeliveryRepository).toErrorOnActivity(deliveryId);
        verify(publisher).toDundieDeliverySplitRollbackQueue(deliveryId);
    }
}
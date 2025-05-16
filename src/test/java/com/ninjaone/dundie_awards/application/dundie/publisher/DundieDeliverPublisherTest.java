package com.ninjaone.dundie_awards.application.dundie.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

class DundieDeliverPublisherTest {

    @InjectMocks
    private DundieDeliverPublisher publisher;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void shouldSendToDundieDeliverySplitQueue() {
        Long id = 1L;
        publisher.toDundieDeliverySplitQueue(id);

        verify(rabbitTemplate).convertAndSend("dundie.delivery.split.exchange", "dundie.delivery.split", id);
    }

    @Test
    void shouldSendToDundieDeliverySplitRollbackQueue() {
        Long id = 2L;
        publisher.toDundieDeliverySplitRollbackQueue(id);

        verify(rabbitTemplate).convertAndSend("dundie.delivery.split.rollback.exchange", "dundie.delivery.split.rollback", id);
    }

    @Test
    void shouldSendToDundieDeliveryQueue() {
        Long id = 3L;
        publisher.toDundieDeliveryQueue(id);

        verify(rabbitTemplate).convertAndSend("dundie.delivery.exchange", "dundie.delivery", id);
    }

    @Test
    void shouldSendToDundieDeliveryRollbackQueue() {
        Long id = 4L;
        publisher.toDundieDeliveryRollbackQueue(id);

        verify(rabbitTemplate).convertAndSend("dundie.delivery.rollback.exchange", "dundie.delivery.rollback", id);
    }

    @Test
    void shouldSendToActivityQueue() {
        Long id = 5L;
        publisher.toActivityQueue(id);

        verify(rabbitTemplate).convertAndSend("activity.exchange", "activity", id);
    }
}
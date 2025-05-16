package com.ninjaone.dundie_awards.application.dundie.delivery;

import com.ninjaone.dundie_awards.application.dundie.publisher.DundieDeliverPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class DundieDeliveryConsumerTest {

    @InjectMocks
    DundieDeliveryConsumer consumer;

    @Mock
    ChunkProcessor chunkProcessor;

    @Mock
    DundieDeliverPublisher publisher;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void shouldReceiveChunkIdAndUseThisToGiveDundie() {
        Long chunkId = 123L;

        consumer.receive(chunkId);

        verify(chunkProcessor).giveOneDundieToChunk(eq(chunkId), any());
    }

    @Test
    void shouldSendMessageToQueueWhenDundieDeliveryFinish() {
        Long deliveryId = 999L;
        DundieDeliveryFinished event = new DundieDeliveryFinished(this, deliveryId);

        consumer.onDundieDeliveryFinished(event);

        verify(publisher).toActivityQueue(deliveryId);
    }

}
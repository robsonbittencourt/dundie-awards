package com.ninjaone.dundie_awards.application.dundie.delivery;

import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

class DundieDeliveryRollbackConsumerTest {

    @InjectMocks
    DundieDeliveryRollbackConsumer consumer;

    @Mock
    ChunkProcessor chunkProcessor;

    @Mock
    DundieDeliveryRepository dundieDeliveryRepository;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void shouldCallTakeBackDundieWhenReceiveChunkIdToRollback() {
        Long chunkId = 42L;

        consumer.rollbackChunk(chunkId);

        verify(chunkProcessor).takeBackOneDundieFromChunk(eq(chunkId), any());
    }
}
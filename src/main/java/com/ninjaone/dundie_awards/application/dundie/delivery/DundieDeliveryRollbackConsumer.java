package com.ninjaone.dundie_awards.application.dundie.delivery;

import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.ninjaone.dundie_awards.infrastructure.config.RabbitConfig.DUNDIE_DELIVERY_ROLLBACK_QUEUE;

@Component
public class DundieDeliveryRollbackConsumer {

    @Autowired
    private ChunkProcessor chunkProcessor;

    @Autowired
    private DundieDeliveryRepository dundieDeliveryRepository;

    @Transactional
    @RabbitListener(queues = DUNDIE_DELIVERY_ROLLBACK_QUEUE)
    public void rollbackChunk(Long chunkId) {
        chunkProcessor.takeBackOneDundieFromChunk(chunkId, dundieDeliveryRepository::toUndone);
    }

}

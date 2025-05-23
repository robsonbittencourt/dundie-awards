package com.ninjaone.dundie_awards.application.dundie.split;

import com.ninjaone.dundie_awards.application.dundie.publisher.DundieDeliverPublisher;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkRepository;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

import static com.ninjaone.dundie_awards.infrastructure.config.RabbitConfig.DUNDIE_DELIVERY_SPLIT_ROLLBACK_QUEUE;
import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkStatus.PENDING_ROLLBACK;
import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryStatusEnum.ERROR_ON_ACTIVITY;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
public class DundieDeliverySplitRollbackConsumer {

    @Autowired
    private DundieDeliveryRepository dundieDeliveryRepository;

    @Autowired
    private DundieDeliveryChunkRepository chunkRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private DundieDeliverPublisher publisher;

    @Transactional
    @RabbitListener(queues = DUNDIE_DELIVERY_SPLIT_ROLLBACK_QUEUE)
    public void rollback(Long dundieDeliveryId) {
        var searchResult = dundieDeliveryRepository.findByIdAndStatusWithLock(dundieDeliveryId, ERROR_ON_ACTIVITY);

        searchResult.ifPresent(dundieDelivery -> {
            dundieDeliveryRepository.toRunning(dundieDelivery);

            chunkRepository.createRollbackChunksToDelivery(dundieDeliveryId);
            List<Long> chunksIds = chunkRepository.findIdsByDundieDeliveryIdAndStatus(dundieDeliveryId, PENDING_ROLLBACK);

            eventPublisher.publishEvent(new DundieDeliverySplitRollbackFinished(this, chunksIds));
        });
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onDundieDeliverySplitRollbackFinished(DundieDeliverySplitRollbackFinished event) {
        for (Long chunkId : event.getChunksIds()) {
            publisher.toDundieDeliveryRollbackQueue(chunkId);
        }
    }
}

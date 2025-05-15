package com.ninjaone.dundie_awards.application.dundie.delivery;

import com.ninjaone.dundie_awards.application.dundie.publisher.DundieDeliverPublisher;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDelivery;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.function.Consumer;

import static com.ninjaone.dundie_awards.infrastructure.config.RabbitConfig.DUNDIE_DELIVERY_QUEUE;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
public class DundieDeliveryConsumer {

    @Autowired
    private ChunkProcessor chunkProcessor;

    @Autowired
    private DundieDeliveryRepository dundieDeliveryRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private DundieDeliverPublisher publisher;

    @Transactional
    @RabbitListener(queues = DUNDIE_DELIVERY_QUEUE)
    public void receive(Long chunkId) {
        Consumer<DundieDelivery> afterAllProcessed = dundieDelivery -> {
            dundieDeliveryRepository.toDelivered(dundieDelivery);
            eventPublisher.publishEvent(new DundieDeliveryFinished(this, dundieDelivery.getId()));
        };

        chunkProcessor.giveOneDundieToChunk(chunkId, afterAllProcessed);
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onDundieDeliveryFinished(DundieDeliveryFinished event) {
        publisher.toActivityQueue(event.getDeliveryDundieId());
    }

}

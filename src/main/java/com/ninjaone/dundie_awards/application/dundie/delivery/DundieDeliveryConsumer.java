package com.ninjaone.dundie_awards.application.dundie.delivery;

import com.ninjaone.dundie_awards.application.dundie.publisher.DundieDeliverPublisher;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunk;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkRepository;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDelivery;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryRepository;
import com.ninjaone.dundie_awards.infrastructure.repository.employee.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.ninjaone.dundie_awards.infrastructure.config.RabbitConfig.DUNDIE_DELIVERY_QUEUE;
import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkStatus.PENDING;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
public class DundieDeliveryConsumer {

    private static final Logger log = LoggerFactory.getLogger(DundieDeliveryConsumer.class);

    @Autowired
    private DundieDeliveryChunkRepository dundieDeliveryChunkRepository;

    @Autowired
    private DundieDeliveryRepository dundieDeliveryRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private DundieDeliverPublisher publisher;

    @Transactional
    @RabbitListener(queues = DUNDIE_DELIVERY_QUEUE)
    public void receive(Long chunkId) {
        var searchResult = dundieDeliveryChunkRepository.findByIdAndStatusWithLock(chunkId, PENDING);

        searchResult.ifPresent(chunk -> {
            addLog(chunk);
            employeeRepository.giveDundie(chunk.getOrganizationId(), chunk.getStartEmployeeId(), chunk.getEndEmployeeId());
            dundieDeliveryChunkRepository.toFinished(chunk);

            checkDundieDeliveryStatus(chunk);
        });
    }

    private static void addLog(DundieDeliveryChunk chunk) {
        log.info("Found a chunk for organization {}. Processing from id {} to {}", chunk.getOrganizationId(), chunk.getStartEmployeeId(), chunk.getEndEmployeeId());
    }

    private void checkDundieDeliveryStatus(DundieDeliveryChunk chunk) {
        DundieDelivery dundieDelivery = chunk.getDundieDelivery();
        boolean exists = dundieDeliveryChunkRepository.hasPendingChunk(dundieDelivery.getId());

        if (!exists) {
            dundieDeliveryRepository.toDelivered(dundieDelivery);
            eventPublisher.publishEvent(new DundieDeliveryFinished(this, dundieDelivery.getId()));
        }
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onDundieDeliveryFinished(DundieDeliveryFinished event) {
        publisher.toActivityQueue(event.getDeliveryDundieId());
    }
}

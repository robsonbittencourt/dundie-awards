package com.ninjaone.dundie_awards.application.dundie.split;

import com.ninjaone.dundie_awards.application.dundie.publisher.DundieDeliverPublisher;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkRepository;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryRepository;
import com.ninjaone.dundie_awards.infrastructure.repository.employee.EmployeeIds;
import com.ninjaone.dundie_awards.infrastructure.repository.employee.EmployeeRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

import static com.ninjaone.dundie_awards.infrastructure.config.RabbitConfig.DUNDIE_DELIVERY_SPLIT_QUEUE;
import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryStatusEnum.PENDING_SPLIT;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
public class DundieDeliverySplitConsumer {

    @Autowired
    private DundieDeliveryRepository dundieDeliveryRepository;

    @Autowired
    private DundieDeliveryChunkRepository dundieDeliveryChunkRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private DundieDeliverPublisher publisher;

    @Transactional
    @RabbitListener(queues = DUNDIE_DELIVERY_SPLIT_QUEUE)
    public void receive(Long dundieDeliveryId) {
        var searchResult = dundieDeliveryRepository.findByIdAndStatusWithLock(dundieDeliveryId, PENDING_SPLIT);

        searchResult.ifPresent(dundieDelivery -> {
            dundieDeliveryRepository.toRunning(dundieDelivery);

            List<EmployeeIds> employeesIds = employeeRepository.findChunksOfEmployees(dundieDelivery.getOrganizationId());
            List<Long> chunksIds = dundieDeliveryChunkRepository.createChunks(dundieDelivery, employeesIds);

            eventPublisher.publishEvent(new DundieDeliverySplitFinished(this, chunksIds));
        });
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onDundieDeliverySplitFinished(DundieDeliverySplitFinished event) {
        for (Long chunkId : event.getChunksIds()) {
            publisher.toDundieDeliveryQueue(chunkId);
        }
    }

}

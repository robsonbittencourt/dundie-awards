package com.ninjaone.dundie_awards.application.dundie.sentinel;

import com.ninjaone.dundie_awards.application.dundie.publisher.DundieDeliverPublisher;
import com.ninjaone.dundie_awards.infrastructure.config.RabbitHealthChecker;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkRepository;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkStatus.PENDING;
import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkStatus.PENDING_ROLLBACK;
import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryStatusEnum.*;

@Component
public class DundieDeliverySentinel {

    private static final Logger log = LoggerFactory.getLogger(DundieDeliverySentinel.class);

    @Value("${app.sentinel.quantity-to-search}")
    private int quantityToSearch;

    @Value("${app.sentinel.delayed-minutes}")
    private int delayedMinutes;

    @Autowired
    private RabbitHealthChecker rabbitHealthChecker;

    @Autowired
    private DundieDeliveryRepository dundieDeliveryRepository;

    @Autowired
    private DundieDeliveryChunkRepository dundieDeliveryChunkRepository;

    @Autowired
    private DundieDeliverPublisher publisher;

    @Scheduled(fixedDelay = 60000L)
    public void restartPendingDeliveries() {
        if (rabbitHealthChecker.isOnline()) {
            var pendingDeliveries = dundieDeliveryRepository.findTopByStatusWithDelay(quantityToSearch, delayedMinutes, PENDING_SPLIT, DELIVERED, ERROR_ON_ACTIVITY);

            log.info("Looking for delayed Dundie Deliveries. Found: {}", pendingDeliveries.size());

            pendingDeliveries.forEach(dundieDelivery -> {
                switch (dundieDelivery.getStatus()) {
                    case PENDING_SPLIT -> publisher.toDundieDeliverySplitQueue(dundieDelivery.getId());
                    case DELIVERED -> publisher.toActivityQueue(dundieDelivery.getId());
                    case ERROR_ON_ACTIVITY -> publisher.toDundieDeliverySplitRollbackQueue(dundieDelivery.getId());
                }
            });
        }
    }

    @Scheduled(fixedDelay = 60000L)
    public void restartPendingDeliveryChunks() {
        if (rabbitHealthChecker.isOnline()) {
            var pendingChunks = dundieDeliveryChunkRepository.findTopPendingChunksWithDelay(quantityToSearch, delayedMinutes, PENDING, PENDING_ROLLBACK);

            log.info("Looking for delayed Dundie Delivery Chunks. Found: {}", pendingChunks.size());

            pendingChunks.forEach(chunk -> {
                switch (chunk.getStatus()) {
                    case PENDING -> publisher.toDundieDeliveryQueue(chunk.getId());
                    case PENDING_ROLLBACK -> publisher.toDundieDeliveryRollbackQueue(chunk.getId());
                }
            });
        }
    }

}

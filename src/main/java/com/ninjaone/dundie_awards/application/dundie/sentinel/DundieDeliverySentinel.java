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
            var pendingDeliveries = dundieDeliveryRepository.findTopByStatusWithMoreThanMinutes(PENDING_SPLIT, quantityToSearch, delayedMinutes);

            log.info("Looking for PENDING_SPLIT Dundie Deliveries. Found: {}", pendingDeliveries.size());

            pendingDeliveries.forEach(dundieDelivery -> publisher.toDundieDeliverySplitQueue(dundieDelivery));
        }
    }

    @Scheduled(fixedDelay = 60000L)
    public void restartPendingDeliveryChunks() {
        if (rabbitHealthChecker.isOnline()) {
            var pendingChunks = dundieDeliveryChunkRepository.findTopPendingChunksWithMoreThanMinutes(PENDING, quantityToSearch, delayedMinutes);

            log.info("Looking for PENDING Dundie Delivery Chunks. Found: {}", pendingChunks.size());

            pendingChunks.forEach(chunk -> publisher.toDundieDeliveryQueue(chunk.getId()));
        }
    }

    @Scheduled(fixedDelay = 60000L)
    public void restartPendingRollbackDeliveryChunks() {
        if (rabbitHealthChecker.isOnline()) {
            var pendingChunks = dundieDeliveryChunkRepository.findTopPendingChunksWithMoreThanMinutes(PENDING_ROLLBACK, quantityToSearch, delayedMinutes);

            log.info("Looking for PENDING_ROLLBACK Dundie Delivery Chunks. Found: {}", pendingChunks.size());

            pendingChunks.forEach(chunk -> publisher.toDundieDeliveryRollbackQueue(chunk.getId()));
        }
    }

    @Scheduled(fixedDelay = 60000L)
    public void restartPendingDeliveriesToActivity() {
        if (rabbitHealthChecker.isOnline()) {
            var deliveriesToActivity = dundieDeliveryRepository.findTopByStatusWithMoreThanMinutes(DELIVERED, quantityToSearch, delayedMinutes);

            log.info("Looking for pending Dundie Deliveries to Activity. Found: {}", deliveriesToActivity.size());

            deliveriesToActivity.forEach(dundieDelivery -> publisher.toActivityQueue(dundieDelivery.getId()));
        }
    }

    @Scheduled(fixedDelay = 60000L)
    public void restartDeliveriesWithErrorOnActivity() {
        if (rabbitHealthChecker.isOnline()) {
            var deliveriesToActivity = dundieDeliveryRepository.findTopByStatusWithMoreThanMinutes(ERROR_ON_ACTIVITY, quantityToSearch, delayedMinutes);

            log.info("Looking for pending Dundie Deliveries with error on Activity. Found: {}", deliveriesToActivity.size());

            deliveriesToActivity.forEach(dundieDelivery -> publisher.toDundieDeliverySplitRollbackQueue(dundieDelivery.getId()));
        }
    }

}

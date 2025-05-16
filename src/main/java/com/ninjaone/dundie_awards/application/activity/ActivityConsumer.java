package com.ninjaone.dundie_awards.application.activity;

import com.ninjaone.dundie_awards.application.dundie.publisher.DundieDeliverPublisher;
import com.ninjaone.dundie_awards.infrastructure.helper.TransactionHelper;
import com.ninjaone.dundie_awards.infrastructure.repository.activity.Activity;
import com.ninjaone.dundie_awards.infrastructure.repository.activity.ActivityRepository;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.ninjaone.dundie_awards.infrastructure.config.RabbitConfig.ACTIVITY_QUEUE;
import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryStatusEnum.DELIVERED;
import static java.time.LocalDateTime.now;
import static org.springframework.transaction.event.TransactionPhase.AFTER_ROLLBACK;

@Component
public class ActivityConsumer {

    private static final Logger log = LoggerFactory.getLogger(ActivityConsumer.class);

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private DundieDeliveryRepository dundieDeliveryRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private DundieDeliverPublisher publisher;

    @Autowired
    private TransactionHelper transactionHelper;

    @Transactional
    @RabbitListener(queues = ACTIVITY_QUEUE)
    void createActivity(Long dundieDeliveryId) {
        try {
            var searchResult = dundieDeliveryRepository.findByIdAndStatusWithLock(dundieDeliveryId, DELIVERED);

            searchResult.ifPresent(dundieDelivery -> {
                Activity activity = new Activity(now(), "Dundie was delivered to organization " + dundieDelivery.getOrganizationId());
                activityRepository.save(activity);
                dundieDeliveryRepository.toFinished(dundieDelivery);

                log.info("Activity received: Event {} - Occurred at: {}", activity.getEvent(), activity.getOccuredAt());
            });
        } catch (Exception ex) {
            log.error("An error occurred on create activity to Dundie Delivery {}", dundieDeliveryId, ex);

            transactionHelper.setRollbackOnly();
            eventPublisher.publishEvent(new ActivityCreationFailed(this, dundieDeliveryId));
        }
    }

    @TransactionalEventListener(phase = AFTER_ROLLBACK)
    void onDundieDeliverySplitFinished(ActivityCreationFailed event) {
        dundieDeliveryRepository.toErrorOnActivity(event.getDundieDeliveryId());
        publisher.toDundieDeliverySplitRollbackQueue(event.getDundieDeliveryId());
    }

}

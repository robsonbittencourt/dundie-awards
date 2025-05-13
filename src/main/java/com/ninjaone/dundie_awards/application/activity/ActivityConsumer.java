package com.ninjaone.dundie_awards.application.activity;

import com.ninjaone.dundie_awards.infrastructure.repository.activity.Activity;
import com.ninjaone.dundie_awards.infrastructure.repository.activity.ActivityRepository;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.ninjaone.dundie_awards.infrastructure.config.RabbitConfig.ACTIVITY_QUEUE;
import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryStatusEnum.DELIVERED;
import static java.time.LocalDateTime.now;

@Component
public class ActivityConsumer {

    private static final Logger log = LoggerFactory.getLogger(ActivityConsumer.class);

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private DundieDeliveryRepository dundieDeliveryRepository;

    @RabbitListener(queues = ACTIVITY_QUEUE)
    public void receive(Long dundieDeliveryId) {
        var searchResult = dundieDeliveryRepository.findByIdAndStatusWithLock(dundieDeliveryId, DELIVERED);

        searchResult.ifPresent(dundieDelivery -> {
            Activity activity = new Activity(now(), "Dundie was delivered to organization " + dundieDelivery.getOrganizationId());
            log.info("Activity received: Event {} - Occurred at: {}", activity.getEvent(), activity.getOccuredAt());

            activityRepository.save(activity);
            dundieDeliveryRepository.toFinished(dundieDelivery);
        });
    }

}

package com.ninjaone.dundie_awards.application.activity;

import com.ninjaone.dundie_awards.infrastructure.config.RabbitConfig;
import com.ninjaone.dundie_awards.infrastructure.repository.activity.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ActivityConsumer {

    private static final Logger log = LoggerFactory.getLogger(ActivityConsumer.class);

    @RabbitListener(queues = RabbitConfig.ACTIVITY_QUEUE)
    public void receive(Activity activity) {
        log.info("Activity received: Event {} - Occurred at: {}", activity.getEvent(), activity.getOccuredAt());
    }

    @RabbitListener(queues = RabbitConfig.LOG_QUEUE)
    public void log(Activity activity) {
        log.info("Log: Event {} - Occurred at: {}", activity.getEvent(), activity.getOccuredAt());
    }
}

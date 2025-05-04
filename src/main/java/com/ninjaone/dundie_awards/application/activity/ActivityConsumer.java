package com.ninjaone.dundie_awards.application.activity;

import com.ninjaone.dundie_awards.infrastructure.config.RabbitConfig;
import com.ninjaone.dundie_awards.infrastructure.repository.activity.Activity;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ActivityConsumer {

    @RabbitListener(queues = RabbitConfig.ACTIVITY_QUEUE)
    public void receive(Activity activity) {
        System.out.println("Activity received: Event " + activity.getEvent() + " - Occurred at: " + activity.getOccuredAt());
    }

    @RabbitListener(queues = RabbitConfig.LOG_QUEUE)
    public void log(Activity activity) {
        System.out.println("Log: Event " + activity.getEvent() + " - Occurred at: " + activity.getOccuredAt());
    }
}

package com.ninjaone.dundie_awards.application.activity;

import com.ninjaone.dundie_awards.infrastructure.repository.activity.Activity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.ninjaone.dundie_awards.infrastructure.config.RabbitConfig.ACTIVITY_EXCHANGE;

@Component
public class ActivityProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(Activity activity) {
        rabbitTemplate.convertAndSend(ACTIVITY_EXCHANGE, "", activity);
    }
}

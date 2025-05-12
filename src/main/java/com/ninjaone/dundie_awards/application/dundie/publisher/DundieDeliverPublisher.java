package com.ninjaone.dundie_awards.application.dundie.publisher;

import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDelivery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.ninjaone.dundie_awards.infrastructure.config.RabbitConfig.*;

@Component
public class DundieDeliverPublisher {

    private static final Logger log = LoggerFactory.getLogger(DundieDeliverPublisher.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void toDundieDeliverySplitQueue(DundieDelivery dundieDelivery) {
        sendToQueue(DUNDIE_DELIVERY_SPLIT_EXCHANGE, DUNDIE_DELIVERY_SPLIT_ROUTING_KEY, DUNDIE_DELIVERY_SPLIT_QUEUE, dundieDelivery.getId());
    }

    public void toDundieDeliveryQueue(Long chunkId) {
        sendToQueue(DUNDIE_DELIVERY_EXCHANGE, DUNDIE_DELIVERY_ROUTING_KEY, DUNDIE_DELIVERY_QUEUE, chunkId);
    }

    public void toActivityQueue(Long dundieDeliveryId) {
        sendToQueue(ACTIVITY_EXCHANGE, ACTIVITY_ROUTING_KEY, ACTIVITY_QUEUE, dundieDeliveryId);
    }

    private void sendToQueue(String exchange, String routingKey, String queue, long id) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, id);
        } catch (Exception ex) {
            log.warn("Error sending to queue {}. Compensation will be executed later.", queue, ex);
        }
    }
}

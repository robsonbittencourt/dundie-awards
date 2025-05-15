package com.ninjaone.dundie_awards.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String ACTIVITY_QUEUE = "activity.queue";
    public static final String ACTIVITY_EXCHANGE = "activity.exchange";
    public static final String ACTIVITY_ROUTING_KEY = "activity";

    public static final String DUNDIE_DELIVERY_SPLIT_QUEUE = "dundie.delivery.split.queue";
    public static final String DUNDIE_DELIVERY_SPLIT_EXCHANGE = "dundie.delivery.split.exchange";
    public static final String DUNDIE_DELIVERY_SPLIT_ROUTING_KEY = "dundie.delivery.split";

    public static final String DUNDIE_DELIVERY_QUEUE = "dundie.delivery.queue";
    public static final String DUNDIE_DELIVERY_EXCHANGE = "dundie.delivery.exchange";
    public static final String DUNDIE_DELIVERY_ROUTING_KEY = "dundie.delivery";

    public static final String DUNDIE_DELIVERY_SPLIT_ROLLBACK_QUEUE = "dundie.delivery.split.rollback.queue";
    public static final String DUNDIE_DELIVERY_SPLIT_ROLLBACK_EXCHANGE = "dundie.delivery.split.rollback.exchange";
    public static final String DUNDIE_DELIVERY_SPLIT_ROLLBACK_ROUTING_KEY = "dundie.delivery.split.rollback";

    public static final String DUNDIE_DELIVERY_ROLLBACK_QUEUE = "dundie.delivery.rollback.queue";
    public static final String DUNDIE_DELIVERY_ROLLBACK_EXCHANGE = "dundie.delivery.rollback.exchange";
    public static final String DUNDIE_DELIVERY_ROLLBACK_ROUTING_KEY = "dundie.delivery.rollback";

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public Queue activityQueue() {
        return new Queue(ACTIVITY_QUEUE);
    }

    @Bean
    public DirectExchange activityExchange() {
        return new DirectExchange(ACTIVITY_EXCHANGE);
    }

    @Bean
    public Binding bindingActivityQueue() {
        return BindingBuilder.bind(activityQueue())
            .to(activityExchange())
            .with(ACTIVITY_ROUTING_KEY);
    }

    @Bean
    public Queue dundieDeliverySplitQueue() {
        return new Queue(DUNDIE_DELIVERY_SPLIT_QUEUE);
    }

    @Bean
    public DirectExchange dundieDeliverySplitExchange() {
        return new DirectExchange(DUNDIE_DELIVERY_SPLIT_EXCHANGE);
    }

    @Bean
    public Binding bindingDundieDeliverySplitQueue() {
        return BindingBuilder.bind(dundieDeliverySplitQueue())
            .to(dundieDeliverySplitExchange())
            .with(DUNDIE_DELIVERY_SPLIT_ROUTING_KEY);
    }

    @Bean
    public Queue dundieDeliveryQueue() {
        return new Queue(DUNDIE_DELIVERY_QUEUE);
    }

    @Bean
    public DirectExchange dundieDeliveryExchange() {
        return new DirectExchange(DUNDIE_DELIVERY_EXCHANGE);
    }

    @Bean
    public Binding bindingDundieDeliveryQueue() {
        return BindingBuilder.bind(dundieDeliveryQueue())
            .to(dundieDeliveryExchange())
            .with(DUNDIE_DELIVERY_ROUTING_KEY);
    }

    @Bean
    public Queue dundieDeliverySplitRollbackQueue() {
        return new Queue(DUNDIE_DELIVERY_SPLIT_ROLLBACK_QUEUE);
    }

    @Bean
    public DirectExchange dundieDeliverySplitRollbackExchange() {
        return new DirectExchange(DUNDIE_DELIVERY_SPLIT_ROLLBACK_EXCHANGE);
    }

    @Bean
    public Binding bindingDundieDeliverySplitRollbackQueue() {
        return BindingBuilder.bind(dundieDeliverySplitRollbackQueue())
            .to(dundieDeliverySplitRollbackExchange())
            .with(DUNDIE_DELIVERY_SPLIT_ROLLBACK_ROUTING_KEY);
    }

    @Bean
    public Queue dundieDeliveryRollbackQueue() {
        return new Queue(DUNDIE_DELIVERY_ROLLBACK_QUEUE);
    }

    @Bean
    public DirectExchange dundieDeliveryRollbackExchange() {
        return new DirectExchange(DUNDIE_DELIVERY_ROLLBACK_EXCHANGE);
    }

    @Bean
    public Binding bindingDundieDeliveryRollbackQueue() {
        return BindingBuilder.bind(dundieDeliveryRollbackQueue())
            .to(dundieDeliveryRollbackExchange())
            .with(DUNDIE_DELIVERY_ROLLBACK_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}

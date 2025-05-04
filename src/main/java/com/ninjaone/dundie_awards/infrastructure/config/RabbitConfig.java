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

    public static final String ACTIVITY_EXCHANGE = "activity.exchange";

    public static final String ACTIVITY_QUEUE = "activity.queue";
    public static final String LOG_QUEUE = "log.queue";

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public Queue activityQueue() {
        return new Queue(ACTIVITY_QUEUE);
    }

    @Bean
    public Queue logQueue() {
        return new Queue(LOG_QUEUE);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("activity.exchange");
    }

    @Bean
    public Binding bindingActivityQueue() {
        return BindingBuilder.bind(activityQueue())
            .to(fanoutExchange());
    }

    @Bean
    public Binding bindingLogQueue() {
        return BindingBuilder.bind(logQueue())
            .to(fanoutExchange());
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

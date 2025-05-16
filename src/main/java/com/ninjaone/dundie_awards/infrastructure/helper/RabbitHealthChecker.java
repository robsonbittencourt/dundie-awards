package com.ninjaone.dundie_awards.infrastructure.helper;

import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitHealthChecker {

    @Autowired
    private ConnectionFactory connectionFactory;

    public boolean isOnline() {
        try {
            Connection connection = connectionFactory.createConnection();
            return connection.isOpen();
        } catch (Exception e) {
            return false;
        }
    }
}

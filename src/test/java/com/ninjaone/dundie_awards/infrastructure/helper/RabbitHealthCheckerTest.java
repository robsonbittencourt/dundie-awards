package com.ninjaone.dundie_awards.infrastructure.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class RabbitHealthCheckerTest {

    @InjectMocks
    private RabbitHealthChecker healthChecker;

    @Mock
    private ConnectionFactory connectionFactory;

    @Mock
    private Connection connection;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void shouldReturnTrueWhenConnectionIsOpen() {
        when(connectionFactory.createConnection()).thenReturn(connection);
        when(connection.isOpen()).thenReturn(true);

        boolean result = healthChecker.isOnline();

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenConnectionIsClosed() {
        when(connectionFactory.createConnection()).thenReturn(connection);
        when(connection.isOpen()).thenReturn(false);

        boolean result = healthChecker.isOnline();

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenExceptionOccurs() {
        when(connectionFactory.createConnection()).thenThrow(new RuntimeException("Connection error"));

        boolean result = healthChecker.isOnline();

        assertThat(result).isFalse();
        verify(connectionFactory).createConnection();
    }

}
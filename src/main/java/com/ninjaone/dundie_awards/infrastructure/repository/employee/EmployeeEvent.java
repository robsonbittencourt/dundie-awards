package com.ninjaone.dundie_awards.infrastructure.repository.employee;

import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

public class EmployeeEvent extends ApplicationEvent {

    private final LocalDateTime occuredAt;
    private final String event;

    public EmployeeEvent(Object source, String event) {
        super(source);
        this.occuredAt = LocalDateTime.now();
        this.event = event;
    }

    public LocalDateTime getOccuredAt() {
        return occuredAt;
    }

    public String getEvent() {
        return event;
    }
}

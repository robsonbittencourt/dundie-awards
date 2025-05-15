package com.ninjaone.dundie_awards.application.activity;

import org.springframework.context.ApplicationEvent;

class ActivityCreationFailed extends ApplicationEvent {

    private final Long dundieDeliveryId;

    public ActivityCreationFailed(Object source, Long dundieDeliveryId) {
        super(source);
        this.dundieDeliveryId = dundieDeliveryId;
    }

    public Long getDundieDeliveryId() {
        return dundieDeliveryId;
    }
}

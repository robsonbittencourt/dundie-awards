package com.ninjaone.dundie_awards.application.dundie.delivery;

import org.springframework.context.ApplicationEvent;

public class DundieDeliveryFinished extends ApplicationEvent {

    private final Long deliveryDundieId;

    public DundieDeliveryFinished(Object source, Long deliveryDundieId) {
        super(source);
        this.deliveryDundieId = deliveryDundieId;
    }

    public Long getDeliveryDundieId() {
        return deliveryDundieId;
    }
}

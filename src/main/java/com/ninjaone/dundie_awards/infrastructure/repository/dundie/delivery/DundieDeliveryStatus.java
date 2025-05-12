package com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "dundie_delivery_status")
public class DundieDeliveryStatus {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne
    private DundieDelivery dundieDelivery;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(STRING)
    @Column(name = "status")
    private DundieDeliveryStatusEnum status;

    public DundieDeliveryStatus() {
    }

    public DundieDeliveryStatus(DundieDelivery dundieDelivery, DundieDeliveryStatusEnum status) {
        this.dundieDelivery = dundieDelivery;
        this.createdAt = LocalDateTime.now();
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public DundieDelivery getDundieDelivery() {
        return dundieDelivery;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public DundieDeliveryStatusEnum getStatus() {
        return status;
    }

}

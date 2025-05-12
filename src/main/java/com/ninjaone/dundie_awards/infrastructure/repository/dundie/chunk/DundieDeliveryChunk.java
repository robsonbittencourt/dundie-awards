package com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk;

import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDelivery;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryStatusEnum;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dundie_delivery_chunk")
public class DundieDeliveryChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private DundieDelivery dundieDelivery;

    @Column(name = "start_employee_id")
    private Long startEmployeeId;

    @Column(name = "end_employee_id")
    private Long endEmployeeId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DundieDeliveryStatusEnum status;

    public DundieDeliveryChunk() {
    }

    public DundieDeliveryChunk(DundieDelivery dundieDelivery, Long startEmployeeId, Long endEmployeeId) {
        this.dundieDelivery = dundieDelivery;
        this.startEmployeeId = startEmployeeId;
        this.endEmployeeId = endEmployeeId;
        this.createdAt = LocalDateTime.now();
        this.status = DundieDeliveryStatusEnum.PENDING_SPLIT;
    }

    public long getId() {
        return id;
    }

    public DundieDelivery getDundieDelivery() {
        return dundieDelivery;
    }

    public Long getStartEmployeeId() {
        return startEmployeeId;
    }

    public Long getEndEmployeeId() {
        return endEmployeeId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public Long getOrganizationId() {
        return dundieDelivery.getOrganizationId();
    }

    public DundieDeliveryStatusEnum getStatus() {
        return status;
    }

    public void setStatus(DundieDeliveryStatusEnum status) {
        this.status = status;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }
}

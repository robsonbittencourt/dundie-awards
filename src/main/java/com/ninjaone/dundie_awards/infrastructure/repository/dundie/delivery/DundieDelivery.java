package com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery;

import com.ninjaone.dundie_awards.infrastructure.repository.organization.Organization;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryStatusEnum.PENDING_SPLIT;
import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;

@Entity
@Table(name = "dundie_delivery")
public class DundieDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "identifier")
    private UUID identifier;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @ManyToOne
    private Organization organization;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DundieDeliveryStatusEnum status;

    public DundieDelivery() {
    }

    public DundieDelivery(Long organizationId) {
        this.identifier = randomUUID();
        this.createdAt = now();
        this.updatedAt = now();
        this.organization = new Organization(organizationId);
        this.status = PENDING_SPLIT;
    }

    public long getId() {
        return id;
    }

    public UUID getIdentifier() {
        return identifier;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Organization getOrganization() {
        return organization;
    }

    public Long getOrganizationId() {
        return organization.getId();
    }

    public DundieDeliveryStatusEnum getStatus() {
        return status;
    }

    public void setStatus(DundieDeliveryStatusEnum status) {
        this.status = status;
    }
}

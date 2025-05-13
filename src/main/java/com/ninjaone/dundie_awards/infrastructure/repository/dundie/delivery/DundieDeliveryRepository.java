package com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryStatusEnum.*;
import static java.time.LocalDateTime.now;

@Repository
public class DundieDeliveryRepository {

    @Autowired
    private DundieDeliveryJpaRepository jpaRepository;

    @Autowired
    private DundieDeliveryStatusJpaRepository dundieDeliveryStatusJpaRepository;

    @Transactional
    public DundieDelivery create(Long organizationId) {
        DundieDelivery dundieDelivery = jpaRepository.save(new DundieDelivery(organizationId));
        createDundieDeliveryStatus(dundieDelivery, PENDING_SPLIT);

        return dundieDelivery;
    }

    @Transactional
    public Optional<DundieDelivery> findByIdAndStatusWithLock(Long id, DundieDeliveryStatusEnum status) {
        return jpaRepository.findByIdAndStatusWithLock(id, status);
    }

    @Transactional
    public void toRunning(DundieDelivery dundieDelivery) {
        dundieDelivery.setStatus(RUNNING);
        dundieDelivery.setUpdatedAt(now());
        jpaRepository.save(dundieDelivery);

        createDundieDeliveryStatus(dundieDelivery, RUNNING);
    }

    @Transactional
    public void toDelivered(DundieDelivery dundieDelivery) {
        dundieDelivery.setStatus(DELIVERED);
        dundieDelivery.setUpdatedAt(now());
        jpaRepository.save(dundieDelivery);

        createDundieDeliveryStatus(dundieDelivery, DELIVERED);
    }

    @Transactional
    public void toFinished(DundieDelivery dundieDelivery) {
        dundieDelivery.setStatus(FINISHED);
        dundieDelivery.setUpdatedAt(now());
        dundieDelivery.setFinishedAt(now());
        jpaRepository.save(dundieDelivery);

        createDundieDeliveryStatus(dundieDelivery, FINISHED);
    }

    @Transactional
    public List<DundieDelivery> findTopByStatusWithMoreThanMinutes(DundieDeliveryStatusEnum status, int quantity, int minutes) {
        return jpaRepository.findTopByStatusWithMoreThanMinutes(status.name(), quantity, minutes);
    }

    private void createDundieDeliveryStatus(DundieDelivery dundieDelivery, DundieDeliveryStatusEnum deliveryStatus) {
        DundieDeliveryStatus dundieDeliveryStatus = new DundieDeliveryStatus(dundieDelivery, deliveryStatus);
        dundieDeliveryStatusJpaRepository.save(dundieDeliveryStatus);
    }

}

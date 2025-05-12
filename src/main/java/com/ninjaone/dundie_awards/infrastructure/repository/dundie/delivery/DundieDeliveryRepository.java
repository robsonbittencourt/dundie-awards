package com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
        jpaRepository.save(dundieDelivery);

        createDundieDeliveryStatus(dundieDelivery, RUNNING);
    }

    @Transactional
    public void toFinished(DundieDelivery dundieDelivery) {
        dundieDelivery.setStatus(FINISHED);
        dundieDelivery.setFinishedAt(now());
        jpaRepository.save(dundieDelivery);

        createDundieDeliveryStatus(dundieDelivery, FINISHED);
    }

    private void createDundieDeliveryStatus(DundieDelivery dundieDelivery, DundieDeliveryStatusEnum deliveryStatus) {
        DundieDeliveryStatus dundieDeliveryStatus = new DundieDeliveryStatus(dundieDelivery, deliveryStatus);
        dundieDeliveryStatusJpaRepository.save(dundieDeliveryStatus);
    }

}

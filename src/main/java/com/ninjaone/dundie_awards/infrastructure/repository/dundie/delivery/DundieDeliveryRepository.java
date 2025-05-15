package com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryStatusEnum.*;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.stream;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Repository
public class DundieDeliveryRepository {

    @Autowired
    private DundieDeliveryJpaRepository jpaRepository;

    @Autowired
    private DundieDeliveryStatusJpaRepository dundieDeliveryStatusJpaRepository;

    public Optional<DundieDelivery> findByIdentifier(UUID identifier) {
        return jpaRepository.findByIdentifier(identifier);
    }

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
        updateStatus(dundieDelivery, RUNNING);
    }

    @Transactional
    public void toDelivered(DundieDelivery dundieDelivery) {
        updateStatus(dundieDelivery, DELIVERED);
    }

    @Transactional
    public void toFinished(DundieDelivery dundieDelivery) {
        dundieDelivery.setFinishedAt(now());
        updateStatus(dundieDelivery, FINISHED);
    }

    @Transactional(propagation = REQUIRES_NEW)
    public void toErrorOnActivity(Long dundieDeliveryId) {
        var searchResult = jpaRepository.findById(dundieDeliveryId);

        searchResult.ifPresent(dundieDelivery -> {
            updateStatus(dundieDelivery, ERROR_ON_ACTIVITY);
        });
    }

    @Transactional
    public void toUndone(DundieDelivery dundieDelivery) {
        dundieDelivery.setFinishedAt(now());
        updateStatus(dundieDelivery, UNDONE);
    }

    @Transactional
    public List<DundieDelivery> findTopByStatusWithDelay(int quantity, int minutes, DundieDeliveryStatusEnum ...status) {
        List<String> statusList = stream(status).map(Enum::name).toList();
        return jpaRepository.findTopByStatusWithMoreThanMinutes(quantity, minutes, statusList);
    }

    private void updateStatus(DundieDelivery dundieDelivery, DundieDeliveryStatusEnum deliveryStatus) {
        dundieDelivery.setStatus(deliveryStatus);
        dundieDelivery.setUpdatedAt(now());
        jpaRepository.save(dundieDelivery);

        createDundieDeliveryStatus(dundieDelivery, deliveryStatus);
    }

    private void createDundieDeliveryStatus(DundieDelivery dundieDelivery, DundieDeliveryStatusEnum deliveryStatus) {
        DundieDeliveryStatus dundieDeliveryStatus = new DundieDeliveryStatus(dundieDelivery, deliveryStatus);
        dundieDeliveryStatusJpaRepository.save(dundieDeliveryStatus);
    }

}

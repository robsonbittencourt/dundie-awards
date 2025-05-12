package com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface DundieDeliveryStatusJpaRepository extends JpaRepository<DundieDeliveryStatus, Long> {
}

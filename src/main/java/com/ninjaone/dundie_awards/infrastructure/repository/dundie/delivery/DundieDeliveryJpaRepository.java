package com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

@Repository
public interface DundieDeliveryJpaRepository extends JpaRepository<DundieDelivery, Long> {

    @Query("""
        SELECT d
        FROM DundieDelivery d
        WHERE d.id = :id
            AND d.status = :status
    """)
    @Lock(PESSIMISTIC_WRITE)
    Optional<DundieDelivery> findByIdAndStatusWithLock(@Param("id") Long id, @Param("status") DundieDeliveryStatusEnum status);

}

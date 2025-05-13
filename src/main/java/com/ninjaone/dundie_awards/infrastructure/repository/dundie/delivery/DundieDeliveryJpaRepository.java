package com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

@Repository
interface DundieDeliveryJpaRepository extends JpaRepository<DundieDelivery, Long> {

    @Query("""
        SELECT d
        FROM DundieDelivery d
        WHERE d.id = :id
            AND d.status = :status
    """)
    @Lock(PESSIMISTIC_WRITE)
    Optional<DundieDelivery> findByIdAndStatusWithLock(@Param("id") Long id, @Param("status") DundieDeliveryStatusEnum status);

    @Query(value = """
        SELECT * FROM dundie_delivery
        WHERE status = :status
            AND updated_at < CURRENT_TIMESTAMP - (:minutes * INTERVAL '1 minute')
        ORDER BY updated_at ASC
        LIMIT :quantity
        FOR UPDATE
    """, nativeQuery = true)
    List<DundieDelivery> findTopByStatusWithMoreThanMinutes(@Param("status") String status, @Param("quantity") int quantity, @Param("minutes") int minutes);

}

package com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk;

import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

@Repository
public interface DundieDeliveryChunkJpaRepository extends JpaRepository<DundieDeliveryChunk, Long> {

    @Query("""
        SELECT COUNT(c) > 0
        FROM DundieDeliveryChunk c
        WHERE c.dundieDelivery.id = :dundieDeliveryId
            AND c.status = :status
    """)
    boolean existsByDundieDeliveryIdAndStatus(@Param("dundieDeliveryId") Long dundieDeliveryId, @Param("status") DundieDeliveryStatusEnum status);

    @Query("""
        SELECT d
        FROM DundieDeliveryChunk d
        WHERE d.id = :id
            AND d.status = :status
    """)
    @Lock(PESSIMISTIC_WRITE)
    Optional<DundieDeliveryChunk> findByIdAndStatusWithLock(@Param("id") Long id, @Param("status") DundieDeliveryStatusEnum status);

}

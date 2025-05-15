package com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

@Repository
interface DundieDeliveryChunkJpaRepository extends JpaRepository<DundieDeliveryChunk, Long> {

    @Query("""
        SELECT COUNT(c) > 0
        FROM DundieDeliveryChunk c
        WHERE c.dundieDelivery.id = :dundieDeliveryId
            AND c.status = :status
    """)
    boolean existsByDundieDeliveryIdAndStatus(@Param("dundieDeliveryId") Long dundieDeliveryId, @Param("status") DundieDeliveryChunkStatus status);

    @Query("""
        SELECT d
        FROM DundieDeliveryChunk d
        WHERE d.id = :id
            AND d.status = :status
    """)
    @Lock(PESSIMISTIC_WRITE)
    Optional<DundieDeliveryChunk> findByIdAndStatusWithLock(@Param("id") Long id, @Param("status") DundieDeliveryChunkStatus status);

    @Query(value = """
        SELECT * FROM dundie_delivery_chunk
        WHERE status IN (:status)
            AND created_at < CURRENT_TIMESTAMP - (:minutes * INTERVAL '1 minute')
        ORDER BY created_at ASC
        LIMIT :quantity
        FOR UPDATE
    """, nativeQuery = true)
    List<DundieDeliveryChunk> findTopByStatusWithDelay(@Param("quantity") int quantity, @Param("minutes") int minutes, @Param("status") List<String> status);

    @Modifying
    @Query(value = """
        INSERT INTO dundie_delivery_chunk (dundie_delivery_id, start_employee_id, end_employee_id, created_at, status)
        SELECT dundie_delivery_id, start_employee_id, end_employee_id, NOW(), 'PENDING_ROLLBACK'
        FROM dundie_delivery_chunk
        WHERE dundie_delivery_id = :deliveryId
    """, nativeQuery = true)
    void createRollbackChunksToDelivery(@Param("deliveryId") Long deliveryId);

    @Query("""
        SELECT c.id
        FROM DundieDeliveryChunk c
        WHERE c.status = :status
          AND c.dundieDelivery.id = :deliveryId
    """)
    List<Long> findIdsByStatusAndDundieDeliveryId(DundieDeliveryChunkStatus status, Long deliveryId);

}

package com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk;

import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDelivery;
import com.ninjaone.dundie_awards.infrastructure.repository.employee.EmployeeIds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkStatus.FINISHED;
import static java.time.LocalDateTime.now;

@Repository
public class DundieDeliveryChunkRepository {

    @Autowired
    private DundieDeliveryChunkJpaRepository jpaRepository;

    @Transactional
    public List<Long> createChunks(DundieDelivery dundieDelivery, List<EmployeeIds> employeesIds) {
        List<DundieDeliveryChunk> chunks = employeesIds.stream()
            .map(ids -> new DundieDeliveryChunk(dundieDelivery, ids.getStartEmployeeId(), ids.getEndEmployeeId()))
            .toList();

        jpaRepository.saveAll(chunks);

        return chunks.stream().map(DundieDeliveryChunk::getId).toList();
    }

    @Transactional
    public void createRollbackChunksToDelivery(Long deliveryId) {
        jpaRepository.createRollbackChunksToDelivery(deliveryId);
    }

    @Transactional
    public Optional<DundieDeliveryChunk> findByIdAndStatusWithLock(Long id, DundieDeliveryChunkStatus status) {
        return jpaRepository.findByIdAndStatusWithLock(id, status);
    }

    public List<Long> findIdsByDundieDeliveryIdAndStatus(Long deliveryId, DundieDeliveryChunkStatus status) {
        return jpaRepository.findIdsByStatusAndDundieDeliveryId(status, deliveryId);
    }

    public void toFinished(DundieDeliveryChunk chunk) {
        chunk.setStatus(FINISHED);
        chunk.setFinishedAt(now());
        jpaRepository.save(chunk);
    }

    public boolean hasPendingChunk(Long dundieDeliveryId, DundieDeliveryChunkStatus status) {
        return jpaRepository.existsByDundieDeliveryIdAndStatus(dundieDeliveryId, status);
    }

    @Transactional
    public List<DundieDeliveryChunk> findTopPendingChunksWithMoreThanMinutes(DundieDeliveryChunkStatus status, int quantity, int minutes) {
        return jpaRepository.findTopByStatusWithMoreThanMinutes(status.name(), quantity, minutes);
    }

}

package com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk;

import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDelivery;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryStatusEnum;
import com.ninjaone.dundie_awards.infrastructure.repository.employee.EmployeeIds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryStatusEnum.FINISHED;
import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryStatusEnum.PENDING_SPLIT;
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
    public Optional<DundieDeliveryChunk> findByIdAndStatusWithLock(Long id, DundieDeliveryStatusEnum status) {
        return jpaRepository.findByIdAndStatusWithLock(id, status);
    }

    public void toFinished(DundieDeliveryChunk chunk) {
        if (chunk.getStatus() != PENDING_SPLIT) {
            throw new IllegalStateException("Invalid state");
        }

        chunk.setStatus(FINISHED);
        chunk.setFinishedAt(now());
        jpaRepository.save(chunk);
    }

    public boolean hasPendingChunk(Long dundieDeliveryId) {
        return jpaRepository.existsByDundieDeliveryIdAndStatus(dundieDeliveryId, PENDING_SPLIT);
    }

}

package com.ninjaone.dundie_awards.application.dundie.delivery;

import com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunk;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkRepository;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkStatus;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDelivery;
import com.ninjaone.dundie_awards.infrastructure.repository.employee.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkStatus.PENDING;
import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkStatus.PENDING_ROLLBACK;

@Component
class ChunkProcessor {

    private static final Logger log = LoggerFactory.getLogger(ChunkProcessor.class);

    @Autowired
    private DundieDeliveryChunkRepository dundieDeliveryChunkRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    void giveOneDundieToChunk(Long chunkId, Consumer<DundieDelivery> onFinished) {
        processChunk(chunkId, PENDING, 1, onFinished);
    }

    void takeBackOneDundieFromChunk(Long chunkId, Consumer<DundieDelivery> onFinished) {
        processChunk(chunkId, PENDING_ROLLBACK, -1, onFinished);
    }

    private void processChunk(Long chunkId, DundieDeliveryChunkStatus status, int quantity, Consumer<DundieDelivery> onFinished) {
        var searchResult = dundieDeliveryChunkRepository.findByIdAndStatusWithLock(chunkId, status);

        searchResult.ifPresent(chunk -> {
            giveDundies(chunk, quantity);

            if (allChunksFinished(chunk, status)) {
                onFinished.accept(chunk.getDundieDelivery());
            }
        });
    }

    private void giveDundies(DundieDeliveryChunk chunk, int quantity) {
        addLog(chunk);
        employeeRepository.giveDundie(chunk.getOrganizationId(), chunk.getStartEmployeeId(), chunk.getEndEmployeeId(), quantity);
        dundieDeliveryChunkRepository.toFinished(chunk);
    }

    private static void addLog(DundieDeliveryChunk chunk) {
        log.info("Found a chunk for organization {}. Processing from employee id {} to {}", chunk.getOrganizationId(), chunk.getStartEmployeeId(), chunk.getEndEmployeeId());
    }

    private boolean allChunksFinished(DundieDeliveryChunk chunk, DundieDeliveryChunkStatus status) {
        return !dundieDeliveryChunkRepository.hasPendingChunk(chunk.getDundieDeliveryId(), status);
    }

}

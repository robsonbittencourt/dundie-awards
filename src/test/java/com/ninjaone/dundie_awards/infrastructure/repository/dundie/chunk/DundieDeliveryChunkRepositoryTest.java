package com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk;

import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDelivery;
import com.ninjaone.dundie_awards.infrastructure.repository.employee.EmployeeIds;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkStatus.FINISHED;
import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.chunk.DundieDeliveryChunkStatus.PENDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class DundieDeliveryChunkRepositoryTest {

    @InjectMocks
    private DundieDeliveryChunkRepository repository;

    @Mock
    private DundieDeliveryChunkJpaRepository jpaRepository;

    @Mock
    private DundieDelivery dundieDelivery;

    @Mock
    private EmployeeIds employeeIds1;

    @Mock
    private EmployeeIds employeeIds2;

    @Mock
    private DundieDeliveryChunk chunk;

    @Captor
    private ArgumentCaptor<List<DundieDeliveryChunk>> chunkListCaptor;

    @BeforeEach
    void setUp() {
        openMocks(this);
   }

    @Test
    void shouldSaveAllAndReturnIds() {
        when(employeeIds1.getStartEmployeeId()).thenReturn(1L);
        when(employeeIds1.getEndEmployeeId()).thenReturn(2L);
        when(employeeIds2.getStartEmployeeId()).thenReturn(3L);
        when(employeeIds2.getEndEmployeeId()).thenReturn(4L);

        List<EmployeeIds> employees = List.of(employeeIds1, employeeIds2);
        repository.createChunks(dundieDelivery, employees);

        verify(jpaRepository).saveAll(chunkListCaptor.capture());

        List<DundieDeliveryChunk> capturedChunks = chunkListCaptor.getValue();
        assertThat(capturedChunks).hasSize(2);

        assertThat(capturedChunks.getFirst().getStartEmployeeId()).isEqualTo(1);
        assertThat(capturedChunks.getFirst().getEndEmployeeId()).isEqualTo(2);

        assertThat(capturedChunks.get(1).getStartEmployeeId()).isEqualTo(3);
        assertThat(capturedChunks.get(1).getEndEmployeeId()).isEqualTo(4);
    }

    @Test
    void shouldCreateRollbackChunksToDelivery() {
        Long deliveryId = 1L;

        repository.createRollbackChunksToDelivery(deliveryId);

        verify(jpaRepository).createRollbackChunksToDelivery(deliveryId);
    }

    @Test
    void shouldFindByIdAndStatusWithLock() {
        Long id = 1L;
        DundieDeliveryChunkStatus status = PENDING;
        Optional<DundieDeliveryChunk> expected = Optional.of(chunk);

        when(jpaRepository.findByIdAndStatusWithLock(id, status)).thenReturn(expected);

        Optional<DundieDeliveryChunk> result = repository.findByIdAndStatusWithLock(id, status);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldFindIdsByDundieDeliveryIdAndStatus() {
        Long deliveryId = 1L;
        DundieDeliveryChunkStatus status = PENDING;
        List<Long> expectedIds = List.of(1L, 2L);

        when(jpaRepository.findIdsByStatusAndDundieDeliveryId(status, deliveryId)).thenReturn(expectedIds);

        List<Long> result = repository.findIdsByDundieDeliveryIdAndStatus(deliveryId, status);

        assertThat(result).isEqualTo(expectedIds);
    }

    @Test
    void shouldUpdateChunkToFinished() {
        repository.toFinished(chunk);

        verify(chunk).setStatus(FINISHED);
        verify(chunk).setFinishedAt(any());
        verify(jpaRepository).save(chunk);
    }

    @Test
    void shouldReturnTrueWhenHasPendingChunk() {
        Long deliveryId = 1L;
        DundieDeliveryChunkStatus status = PENDING;

        when(jpaRepository.existsByDundieDeliveryIdAndStatus(deliveryId, status)).thenReturn(true);

        boolean result = repository.hasPendingChunk(deliveryId, status);

        assertThat(result).isTrue();
    }

    @Test
    void shouldFindTopPendingChunksWithDelay() {
        int quantity = 5;
        int minutes = 10;
        DundieDeliveryChunkStatus status1 = PENDING;
        DundieDeliveryChunkStatus status2 = DundieDeliveryChunkStatus.PENDING_ROLLBACK;

        List<DundieDeliveryChunk> expectedList = List.of(chunk);

        when(jpaRepository.findTopByStatusWithDelay(quantity, minutes, List.of(status1.name(), status2.name())))
            .thenReturn(expectedList);

        List<DundieDeliveryChunk> result = repository.findTopPendingChunksWithDelay(quantity, minutes, status1, status2);

        assertThat(result).isEqualTo(expectedList);
    }

}
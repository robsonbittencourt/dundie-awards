package com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryStatusEnum.*;
import static java.util.Optional.of;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class DundieDeliveryRepositoryTest {

    @InjectMocks
    private DundieDeliveryRepository repository;

    @Mock
    private DundieDeliveryJpaRepository jpaRepository;

    @Mock
    private DundieDeliveryStatusJpaRepository statusJpaRepository;

    @Mock
    private DundieDelivery dundieDelivery;

    @Captor
    ArgumentCaptor<DundieDeliveryStatus> statusCapture;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void shouldFindByIdentifier() {
        UUID identifier = randomUUID();
        when(jpaRepository.findByIdentifier(identifier)).thenReturn(of(dundieDelivery));

        Optional<DundieDelivery> result = repository.findByIdentifier(identifier);

        assertThat(result).contains(dundieDelivery);
        verify(jpaRepository).findByIdentifier(identifier);
    }

    @Test
    void shouldSaveDeliveryAndCreateStatus() {
        Long orgId = 1L;

        when(jpaRepository.save(any(DundieDelivery.class))).thenReturn(dundieDelivery);

        DundieDelivery result = repository.create(orgId);

        assertThat(result).isEqualTo(dundieDelivery);
        verify(jpaRepository).save(any(DundieDelivery.class));
        verify(statusJpaRepository).save(statusCapture.capture());

        assertThat(statusCapture.getValue().getStatus()).isEqualTo(PENDING_SPLIT);
    }

    @Test
    void shouldFindByIdAndStatusWithLock() {
        Long id = 10L;
        when(jpaRepository.findByIdAndStatusWithLock(id, PENDING_SPLIT)).thenReturn(of(dundieDelivery));

        Optional<DundieDelivery> result = repository.findByIdAndStatusWithLock(id, PENDING_SPLIT);

        assertThat(result).contains(dundieDelivery);
    }

    @Test
    void shouldUpdateStatusToRunning() {
        repository.toRunning(dundieDelivery);

        verify(jpaRepository).save(dundieDelivery);
        verify(statusJpaRepository).save(statusCapture.capture());

        assertThat(statusCapture.getValue().getStatus()).isEqualTo(RUNNING);
    }

    @Test
    void shouldUpdateStatusToDelivered() {
        repository.toDelivered(dundieDelivery);

        verify(jpaRepository).save(dundieDelivery);
        verify(statusJpaRepository).save(statusCapture.capture());

        assertThat(statusCapture.getValue().getStatus()).isEqualTo(DELIVERED);
    }

    @Test
    void shouldUpdateStatusToFinished() {
        repository.toFinished(dundieDelivery);

        verify(dundieDelivery).setFinishedAt(any());
        verify(jpaRepository).save(dundieDelivery);
        verify(statusJpaRepository).save(statusCapture.capture());

        assertThat(statusCapture.getValue().getStatus()).isEqualTo(FINISHED);
    }

    @Test
    void shouldUpdateStatusToErrorOnActivity() {
        Long id = 42L;
        when(jpaRepository.findById(id)).thenReturn(of(dundieDelivery));

        repository.toErrorOnActivity(id);

        verify(jpaRepository).save(dundieDelivery);
        verify(statusJpaRepository).save(statusCapture.capture());

        assertThat(statusCapture.getValue().getStatus()).isEqualTo(ERROR_ON_ACTIVITY);
    }

    @Test
    void shouldUpdateStatusToUndone() {
        repository.toUndone(dundieDelivery);

        verify(dundieDelivery).setFinishedAt(any());
        verify(jpaRepository).save(dundieDelivery);
        verify(statusJpaRepository).save(statusCapture.capture());

        assertThat(statusCapture.getValue().getStatus()).isEqualTo(UNDONE);
    }

    @Test
    void shouldFindTopByStatusWithDelay() {
        when(jpaRepository.findTopByStatusWithMoreThanMinutes(eq(10), eq(15), anyList())).thenReturn(List.of(dundieDelivery));

        List<DundieDelivery> result = repository.findTopByStatusWithDelay(10, 15, PENDING_SPLIT);

        assertThat(result).containsExactly(dundieDelivery);
        verify(jpaRepository).findTopByStatusWithMoreThanMinutes(eq(10), eq(15), anyList());
    }

}
package com.ninjaone.dundie_awards.application.dundie;

import com.ninjaone.dundie_awards.application.dundie.publisher.DundieDeliverPublisher;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDelivery;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryRepository;
import com.ninjaone.dundie_awards.infrastructure.repository.organization.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class DundieDeliveryManagerTest {

    @InjectMocks
    private DundieDeliveryManager manager;

    @Mock
    private DundieDeliveryRepository dundieDeliveryRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private DundieDeliverPublisher publisher;

    @Mock
    private DundieDelivery dundieDelivery;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void whenOrganizationNotExists() {
        Long orgId = 1L;

        when(organizationRepository.existsById(orgId)).thenReturn(false);

        Optional<UUID> result = manager.giveDundieAwards(orgId);

        assertThat(result).isEmpty();
        verifyNoInteractions(dundieDeliveryRepository, publisher);
    }

    @Test
    void shouldCreateDeliveryAndPublish() {
        Long orgId = 2L;
        UUID fakeUuid = randomUUID();

        when(organizationRepository.existsById(orgId)).thenReturn(true);
        when(dundieDeliveryRepository.create(orgId)).thenReturn(dundieDelivery);
        when(dundieDelivery.getId()).thenReturn(123L);
        when(dundieDelivery.getIdentifier()).thenReturn(fakeUuid);

        Optional<UUID> result = manager.giveDundieAwards(orgId);

        assertThat(result).contains(fakeUuid);
        verify(dundieDeliveryRepository).create(orgId);
        verify(publisher).toDundieDeliverySplitQueue(123L);
    }

}
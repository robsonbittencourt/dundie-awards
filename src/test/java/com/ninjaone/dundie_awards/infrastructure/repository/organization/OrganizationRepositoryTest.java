package com.ninjaone.dundie_awards.infrastructure.repository.organization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class OrganizationRepositoryTest {

    @InjectMocks
    private OrganizationRepository repository;

    @Mock
    private OrganizationJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void shouldReturnTrueWhenOrganizationExists() {
        when(jpaRepository.existsById(1L)).thenReturn(true);

        boolean result = repository.existsById(1L);

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenOrganizationDoesNotExist() {
        when(jpaRepository.existsById(2L)).thenReturn(false);

        boolean result = repository.existsById(2L);

        assertThat(result).isFalse();
    }

}
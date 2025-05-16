package com.ninjaone.dundie_awards.infrastructure.repository.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class EmployeeRepositoryTest {

    @InjectMocks
    private EmployeeRepository repository;

    @Mock
    private EmployeeJpaRepository jpaRepository;

    @Mock
    private DundiesCache dundiesCache;

    @Mock
    private Employee employee;

    @BeforeEach
    void setUp() {
        openMocks(this);
        setField(repository, "chunkSize", 100);
    }

    @Test
    void shouldCreateEmployee() {
        String firstName = "Jim";
        String lastName = "Halpert";
        Long orgId = 1L;

        when(jpaRepository.save(any(Employee.class))).thenReturn(employee);

        Employee result = repository.create(firstName, lastName, orgId);

        assertThat(result).isEqualTo(employee);
    }

    @Test
    void shouldFindById() {
        when(jpaRepository.findById(1L)).thenReturn(of(employee));

        Optional<Employee> result = repository.findById(1L);

        assertThat(result).contains(employee);
    }

    @Test
    void shouldFindAllEmployeesUsingPagination() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Employee> page = new PageImpl<>(List.of(employee));

        when(jpaRepository.findAll(pageable)).thenReturn(page);

        Page<Employee> result = repository.findAll(pageable);

        assertThat(result.getContent()).containsExactly(employee);
    }

    @Test
    void shouldThrowErrorWhenPageSizeIsGreaterThan10() {
        Pageable pageable = PageRequest.of(0, 15);

        assertThrows(IllegalArgumentException.class, () -> repository.findAll(pageable));
    }

    @Test
    void shouldFindChunksOfEmployees() {
        Long orgId = 2L;
        List<EmployeeIds> chunks = List.of(mock(EmployeeIds.class));

        when(jpaRepository.findChunksOfEmployees(orgId, 100)).thenReturn(chunks);

        List<EmployeeIds> result = repository.findChunksOfEmployees(orgId);

        assertThat(result).isEqualTo(chunks);
    }

    @Test
    void shouldReturnEmptyWhenTryUpdateNameButEmployeeDoesNotExists() {
        when(jpaRepository.findById(1L)).thenReturn(empty());

        Optional<Employee> result = repository.updateName(1L, "Pam", "Beesly");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldUpdateEmployeeName() {
        when(jpaRepository.findById(1L)).thenReturn(of(employee));

        Optional<Employee> result = repository.updateName(1L, "Pam", "Beesly");

        assertThat(result).contains(employee);
        verify(employee).setFirstName("Pam");
        verify(employee).setLastName("Beesly");
        verify(jpaRepository).save(employee);
    }

    @Test
    void shouldReturnEmptyWhenTryDeleteButEmployeeDoesNotExists() {
        when(jpaRepository.findById(1L)).thenReturn(empty());

        boolean result = repository.delete(1L);

        assertFalse(result);
    }

    @Test
    void shouldDeleteEmployeeAndResetCache() {
        when(jpaRepository.findById(1L)).thenReturn(of(employee));

        boolean result = repository.delete(1L);

        assertTrue(result);
        verify(jpaRepository).deleteById(1L);
        verify(dundiesCache).resetCounter();
    }

    @Test
    void shouldGiveDundieAndResetCache() {
        repository.giveDundie(1L, 10L, 20L, 5);

        verify(jpaRepository).giveDundie(1L, 10L, 20L, 5);
        verify(dundiesCache).resetCounter();
    }

    @Test
    void shouldReturnDundieCacheFromCacheWhenPresent() {
        when(dundiesCache.getCounter()).thenReturn(of(123L));

        long result = repository.dundieQuantity();

        assertThat(result).isEqualTo(123L);
    }

    @Test
    void shouldFetchAndUpdateWhenCacheEmpty() {
        when(dundiesCache.getCounter()).thenReturn(empty());
        when(jpaRepository.totalDundies()).thenReturn(999L);

        long result = repository.dundieQuantity();

        assertThat(result).isEqualTo(999L);
        verify(dundiesCache).updateCounter(999L);
    }

}
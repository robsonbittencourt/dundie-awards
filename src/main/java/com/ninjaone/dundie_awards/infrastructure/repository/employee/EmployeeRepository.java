package com.ninjaone.dundie_awards.infrastructure.repository.employee;

import com.ninjaone.dundie_awards.infrastructure.repository.organization.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeRepository {

    @Value("${app.dundie-delivery-chunk-size}")
    private int chunkSize;

    @Autowired
    private EmployeeJpaRepository jpaRepository;

    @Autowired
    private DundiesCache dundiesCache;

    @Transactional
    public Employee create(String firstName, String lastName, Long organizationId) {
        Organization organization = new Organization();
        organization.setId(organizationId);

        Employee employee = new Employee(firstName, lastName, organization);
        return jpaRepository.save(employee);
    }

    public Optional<Employee> findById(Long id) {
        return jpaRepository.findById(id);
    }

    public Page<Employee> findAll(Pageable pageable) {
        if (pageable.getPageSize() > 10) {
            throw new IllegalArgumentException("Page size should be less than 10");
        }

        return jpaRepository.findAll(pageable);
    }

    public List<EmployeeIds> findChunksOfEmployees(Long organizationId) {
        return jpaRepository.findChunksOfEmployees(organizationId, chunkSize);
    }

    @Transactional
    public Optional<Employee> updateName(Long id, String firstName, String lastName) {
        Optional<Employee> employee = jpaRepository.findById(id);

        if (employee.isEmpty()) {
            return employee;
        }

        employee.get().setFirstName(firstName);
        employee.get().setLastName(lastName);
        jpaRepository.save(employee.get());

        return employee;
    }

    @Transactional
    public boolean delete(Long id) {
        Employee employee = jpaRepository.findById(id).orElse(null);

        if (employee == null) {
            return false;
        }

        jpaRepository.deleteById(id);
        dundiesCache.resetCounter();

        return true;
    }

    @Transactional
    public void giveDundie(Long organizationId, Long startId, Long endId, int quantity) {
        jpaRepository.giveDundie(organizationId, startId, endId, quantity);
        dundiesCache.resetCounter();
    }

    public long dundieQuantity() {
        return dundiesCache.getCounter()
            .orElseGet(() -> {
                long totalDundies = jpaRepository.totalDundies();
                dundiesCache.updateCounter(totalDundies);
                return totalDundies;
            });
    }

}

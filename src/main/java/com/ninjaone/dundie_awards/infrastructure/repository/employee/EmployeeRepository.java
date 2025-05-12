package com.ninjaone.dundie_awards.infrastructure.repository.employee;

import com.ninjaone.dundie_awards.infrastructure.cache.AwardsRedisCache;
import com.ninjaone.dundie_awards.infrastructure.repository.organization.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeRepository {

    @Value("${app.dundie-delivery-chunk-size}")
    private int chunkSize;

    @Autowired
    private EmployeeJpaRepository employeeRepository;

    @Autowired
    private AwardsRedisCache awardsRedisCache;

    @Transactional
    public Employee create(String firstName, String lastName, Long organizationId) {
        Organization organization = new Organization();
        organization.setId(organizationId);

        Employee employee = new Employee(firstName, lastName, organization);
        return employeeRepository.save(employee);
    }

    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public List<EmployeeIds> findChunksOfEmployees(Long organizationId) {
        return employeeRepository.findChunksOfEmployees(organizationId, chunkSize);
    }

    @Transactional
    public Optional<Employee> updateName(Long id, String firstName, String lastName) {
        Optional<Employee> employee = employeeRepository.findById(id);

        if (employee.isEmpty()) {
            return employee;
        }

        employee.get().setFirstName(firstName);
        employee.get().setLastName(lastName);
        employeeRepository.save(employee.get());

        return employee;
    }

    @Transactional
    public boolean delete(Long id) {
        Employee employee = employeeRepository.findById(id).orElse(null);

        if (employee == null) {
            return false;
        }

        employeeRepository.deleteById(id);

        int actualEmployeeDundies = employee.getDundieAwards() != null ? employee.getDundieAwards() : 0;
        int totalAwards = awardsRedisCache.getCounter().intValue() - actualEmployeeDundies;
        awardsRedisCache.updateCounter(totalAwards);

        return true;
    }

    @Transactional
    public boolean giveDundie(Long organizationId, Long startId, Long endId) {
        int givenDundies = employeeRepository.giveDundie(organizationId, startId, endId);
        boolean dundieWasDelivered = givenDundies > 0;

        if (dundieWasDelivered) {
            awardsRedisCache.increment(givenDundies);
        }

        return dundieWasDelivered;
    }

}

package com.ninjaone.dundie_awards.infrastructure.repository.employee;

import com.ninjaone.dundie_awards.infrastructure.repository.organization.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeRepository {

    @Autowired
    private EmployeeJpaRepository employeeRepository;

    @Autowired
    private AwardsCache cache;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public Employee create(String firstName, String lastName, Long organizationId) {
        Organization organization = new Organization();
        organization.setId(organizationId);

        Employee employee = new Employee(firstName, lastName, organization);
        Employee persistedEmployee = employeeRepository.save(employee);

        eventPublisher.publishEvent(new EmployeeEvent(this, "employee_created"));

        return persistedEmployee;
    }

    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
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

        eventPublisher.publishEvent(new EmployeeEvent(this, "employee_updated"));

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
        int totalAwards = cache.getTotalAwards() - actualEmployeeDundies;
        cache.setTotalAwards(totalAwards);

        eventPublisher.publishEvent(new EmployeeEvent(this, "employee_deleted"));

        return true;
    }

    @Transactional
    public boolean giveDundie(Long employeeId) {
        int givenDundies = employeeRepository.giveDundie(employeeId);
        boolean dundieWasDelivered = givenDundies > 0;

        if (dundieWasDelivered) {
            cache.addOneAward();
            eventPublisher.publishEvent(new EmployeeEvent(this, "dundie_delivered"));
        }

        return dundieWasDelivered;
    }

}

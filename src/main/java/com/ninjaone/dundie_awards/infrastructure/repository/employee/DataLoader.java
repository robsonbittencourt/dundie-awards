package com.ninjaone.dundie_awards.infrastructure.repository.employee;

import com.ninjaone.dundie_awards.infrastructure.repository.organization.Organization;
import com.ninjaone.dundie_awards.infrastructure.repository.organization.OrganizationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
class DataLoader implements CommandLineRunner {

    private final EmployeeJpaRepository employeeRepository;
    private final OrganizationRepository organizationRepository;
    private final AwardsCache awardsCache;

    public DataLoader(EmployeeJpaRepository employeeRepository, OrganizationRepository organizationRepository, AwardsCache awardsCache) {
        this.awardsCache = awardsCache;
        this.employeeRepository = employeeRepository;
        this.organizationRepository = organizationRepository;
    }

    @Override
    public void run(String... args) {
        // uncomment to reseed data
        // employeeRepository.deleteAll();
        // organizationRepository.deleteAll();

        if (employeeRepository.count() == 0) {
            Organization organizationPikashu = new Organization("Pikashu");
            organizationRepository.save(organizationPikashu);

            employeeRepository.save(new Employee("John", "Doe", organizationPikashu));
            employeeRepository.save(new Employee("Jane", "Smith", organizationPikashu));
            employeeRepository.save(new Employee("Creed", "Braton", organizationPikashu));

            Organization organizationSquanchy = new Organization("Squanchy");
            organizationRepository.save(organizationSquanchy);

            employeeRepository.save(new Employee("Michael", "Scott", organizationSquanchy));
            employeeRepository.save(new Employee("Dwight", "Schrute", organizationSquanchy));
            employeeRepository.save(new Employee("Jim", "Halpert", organizationSquanchy));
            employeeRepository.save(new Employee("Pam", "Beesley", organizationSquanchy));
        }

        int totalAwards = employeeRepository.findAll().stream()
                .mapToInt(employee -> Objects.requireNonNullElse(employee.getDundieAwards(), 0))
                .sum();
        this.awardsCache.setTotalAwards(totalAwards);
    }
}

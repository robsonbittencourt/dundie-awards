package com.ninjaone.dundie_awards.application.api.employee;

import com.ninjaone.dundie_awards.infrastructure.repository.employee.Employee;

class EmployeeResponse {

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final Long organizationId;

    public EmployeeResponse(Employee employee) {
        this.id = employee.getId();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.organizationId = employee.getOrganization().getId();
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getOrganizationId() {
        return organizationId;
    }
}
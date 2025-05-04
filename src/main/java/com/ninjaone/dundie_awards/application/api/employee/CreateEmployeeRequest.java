package com.ninjaone.dundie_awards.application.api.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

record CreateEmployeeRequest(
    @NotBlank(message = "First name is required") String firstName,
    @NotBlank(message = "Last name is required") String lastName,
    @NotNull(message = "Organization ID is required") Long organizationId
) {
}

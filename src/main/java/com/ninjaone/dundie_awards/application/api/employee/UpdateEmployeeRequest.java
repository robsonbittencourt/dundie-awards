package com.ninjaone.dundie_awards.application.api.employee;

import jakarta.validation.constraints.NotBlank;

record UpdateEmployeeRequest(
    @NotBlank(message = "First name is required") String firstName,
    @NotBlank(message = "First name is required") String lastName
) {
}

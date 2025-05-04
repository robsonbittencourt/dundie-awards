package com.ninjaone.dundie_awards.application.api.dundie;

import jakarta.validation.constraints.NotNull;

record GiveDundieRequest(@NotNull(message = "Employee ID required") Long employeeId) {
}

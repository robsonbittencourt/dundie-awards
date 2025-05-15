package com.ninjaone.dundie_awards.application.api.dundie;

import java.time.LocalDateTime;
import java.util.UUID;

public record DundieDeliveryStatusResponse(UUID identifier, LocalDateTime createdAt, String status) {
}

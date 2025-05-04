package com.ninjaone.dundie_awards.application.api.error;

import java.util.List;

record ErrorResponse(String message, List<String> errors) {
}

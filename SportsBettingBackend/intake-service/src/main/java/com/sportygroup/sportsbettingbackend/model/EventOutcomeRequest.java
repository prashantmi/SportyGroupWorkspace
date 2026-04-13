package com.sportygroup.sportsbettingbackend.model;

import jakarta.validation.constraints.NotBlank;

public record EventOutcomeRequest(
        @NotBlank String eventId,
        @NotBlank String eventName,
        @NotBlank String eventWinnerId
) {
}

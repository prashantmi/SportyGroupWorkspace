package com.sportygroup.sportsbettingbackend.api;

import jakarta.validation.constraints.NotBlank;

public record EventOutcomeRequest(
        @NotBlank String eventId,
        @NotBlank String eventName,
        @NotBlank String eventWinnerId
) {
}

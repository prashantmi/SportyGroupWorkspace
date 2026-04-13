package com.sportygroup.sportsbettingbackend.domain;

public record EventOutcomeMessage(
        String eventId,
        String eventName,
        String eventWinnerId
) {
}

package com.sportygroup.sportsbettingbackend.api;

public record PublishEventOutcomeResponse(
        String eventId,
        String status,
        String topic
) {
}

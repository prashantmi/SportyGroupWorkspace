package com.sportygroup.sportsbettingbackend.model;

public record PublishEventOutcomeResponse(
        String eventId,
        String status,
        String topic
) {
}

package com.sportygroup.sportsbettingbackend.application;

import com.sportygroup.sportsbettingbackend.domain.EventOutcomeMessage;
import org.springframework.stereotype.Service;

@Service
public class EventOutcomePublisherService {

    private final EventOutcomeMessagePublisher eventOutcomeMessagePublisher;

    public EventOutcomePublisherService(EventOutcomeMessagePublisher eventOutcomeMessagePublisher) {
        this.eventOutcomeMessagePublisher = eventOutcomeMessagePublisher;
    }

    public EventOutcomeMessage publish(String eventId, String eventName, String eventWinnerId) {
        EventOutcomeMessage message = new EventOutcomeMessage(
                eventId,
                eventName,
                eventWinnerId
        );
        eventOutcomeMessagePublisher.publish(message);
        return message;
    }
}

package com.sportygroup.sportsbettingbackend.application;

import com.sportygroup.sportsbettingbackend.domain.EventOutcomeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EventOutcomePublisherService {

    private static final Logger log = LoggerFactory.getLogger(EventOutcomePublisherService.class);

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
        log.info("Published event outcome to Kafka: {}", message);
        return message;
    }
}

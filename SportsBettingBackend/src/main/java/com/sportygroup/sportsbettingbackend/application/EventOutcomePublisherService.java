package com.sportygroup.sportsbettingbackend.application;

import com.sportygroup.sportsbettingbackend.api.EventOutcomeRequest;
import com.sportygroup.sportsbettingbackend.domain.EventOutcomeMessage;
import com.sportygroup.sportsbettingbackend.messaging.kafka.KafkaEventOutcomePublisher;
import org.springframework.stereotype.Service;

@Service
public class EventOutcomePublisherService {

    private final KafkaEventOutcomePublisher kafkaEventOutcomePublisher;

    public EventOutcomePublisherService(KafkaEventOutcomePublisher kafkaEventOutcomePublisher) {
        this.kafkaEventOutcomePublisher = kafkaEventOutcomePublisher;
    }

    public EventOutcomeMessage publish(EventOutcomeRequest request) {
        EventOutcomeMessage message = new EventOutcomeMessage(
                request.eventId(),
                request.eventName(),
                request.eventWinnerId()
        );
        kafkaEventOutcomePublisher.publish(message);
        return message;
    }
}

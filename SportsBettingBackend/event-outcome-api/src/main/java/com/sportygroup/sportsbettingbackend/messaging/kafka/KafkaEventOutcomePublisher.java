package com.sportygroup.sportsbettingbackend.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportygroup.sportsbettingbackend.application.EventOutcomeMessagePublisher;
import com.sportygroup.sportsbettingbackend.config.AppProperties;
import com.sportygroup.sportsbettingbackend.domain.EventOutcomeMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventOutcomePublisher implements EventOutcomeMessagePublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;

    public KafkaEventOutcomePublisher(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            AppProperties appProperties
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.appProperties = appProperties;
    }

    @Override
    public void publish(EventOutcomeMessage message) {
        kafkaTemplate.send(
                appProperties.getKafka().getEventOutcomesTopic(),
                message.eventId(),
                writeValue(message)
        );
    }

    private String writeValue(EventOutcomeMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize event outcome", exception);
        }
    }
}

package com.sportygroup.sportsbettingbackend.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportygroup.sportsbettingbackend.application.BetSettlementOrchestratorService;
import com.sportygroup.sportsbettingbackend.domain.EventOutcomeMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventOutcomeConsumer {

    private final ObjectMapper objectMapper;
    private final BetSettlementOrchestratorService betSettlementOrchestratorService;

    public KafkaEventOutcomeConsumer(
            ObjectMapper objectMapper,
            BetSettlementOrchestratorService betSettlementOrchestratorService
    ) {
        this.objectMapper = objectMapper;
        this.betSettlementOrchestratorService = betSettlementOrchestratorService;
    }

    @KafkaListener(topics = "${app.kafka.event-outcomes-topic}")
    public void consume(String payload) {
        betSettlementOrchestratorService.process(readValue(payload));
    }

    private EventOutcomeMessage readValue(String payload) {
        try {
            return objectMapper.readValue(payload, EventOutcomeMessage.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to deserialize event outcome message", exception);
        }
    }
}

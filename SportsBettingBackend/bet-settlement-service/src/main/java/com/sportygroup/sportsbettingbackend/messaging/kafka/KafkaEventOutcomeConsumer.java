package com.sportygroup.sportsbettingbackend.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportygroup.sportsbettingbackend.application.BetSettlementOrchestratorService;
import com.sportygroup.sportsbettingbackend.domain.EventOutcomeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventOutcomeConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventOutcomeConsumer.class);

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
        EventOutcomeMessage outcome = readValue(payload);
        log.info("Consumed Kafka event outcome message: {}", outcome);
        betSettlementOrchestratorService.process(outcome);
    }

    private EventOutcomeMessage readValue(String payload) {
        try {
            return objectMapper.readValue(payload, EventOutcomeMessage.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to deserialize event outcome message", exception);
        }
    }
}

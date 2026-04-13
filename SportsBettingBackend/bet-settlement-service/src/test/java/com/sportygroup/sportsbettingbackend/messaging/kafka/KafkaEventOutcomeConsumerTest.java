package com.sportygroup.sportsbettingbackend.messaging.kafka;

import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportygroup.sportsbettingbackend.application.BetSettlementOrchestratorService;
import com.sportygroup.sportsbettingbackend.domain.EventOutcomeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KafkaEventOutcomeConsumerTest {

    @Mock
    private BetSettlementOrchestratorService betSettlementOrchestratorService;

    @Test
    void shouldDeserializePayloadAndDelegateToOrchestrator() throws Exception {
        KafkaEventOutcomeConsumer consumer = new KafkaEventOutcomeConsumer(
                new ObjectMapper(),
                betSettlementOrchestratorService
        );

        consumer.consume("""
                {"eventId":"event-100","eventName":"Match Winner","eventWinnerId":"winner-1"}
                """);

        verify(betSettlementOrchestratorService).process(
                new EventOutcomeMessage("event-100", "Match Winner", "winner-1")
        );
    }
}

package com.sportygroup.sportsbettingbackend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportygroup.sportsbettingbackend.application.BetSettlementMessagePublisher;
import com.sportygroup.sportsbettingbackend.application.BetSettlementFinalizerService;
import com.sportygroup.sportsbettingbackend.domain.Bet;
import com.sportygroup.sportsbettingbackend.domain.BetSettlementMessage;
import com.sportygroup.sportsbettingbackend.domain.BetStatus;
import com.sportygroup.sportsbettingbackend.domain.BetType;
import com.sportygroup.sportsbettingbackend.model.BookBetRequest;
import com.sportygroup.sportsbettingbackend.model.EventOutcomeRequest;
import com.sportygroup.sportsbettingbackend.persistence.BetRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, topics = "event-outcomes")
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.autoconfigure.exclude=org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration"
})
class EventOutcomeFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private BetSettlementFinalizerService betSettlementFinalizerService;

    @MockitoBean
    private BetSettlementMessagePublisher betSettlementMessagePublisher;

    @BeforeEach
    void setUp() {
        doAnswer(invocation -> {
            BetSettlementMessage message = invocation.getArgument(0);
            betSettlementFinalizerService.finalizeSettlement(message);
            return null;
        }).when(betSettlementMessagePublisher).publish(any(BetSettlementMessage.class));
    }

    @Test
    void shouldPublishOutcomeConsumeFromKafkaAndFinalizeBetSettlement() throws Exception {
        mockMvc.perform(post("/api/event-outcomes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new EventOutcomeRequest("event-100", "Match Winner", "winner-1")
                        )))
                .andExpect(status().isAccepted());

        verify(betSettlementMessagePublisher, timeout(5000).times(3)).publish(any(BetSettlementMessage.class));

        List<Bet> eventBets = betRepository.findByEventIdAndStatus("event-100", BetStatus.SETTLED);
        assertThat(eventBets).hasSize(3);
        assertThat(eventBets)
                .extracting(Bet::getStatus)
                .containsOnly(BetStatus.SETTLED);
    }

    @Test
    void shouldBookBetAndSettleItThroughExistingOutcomeFlow() throws Exception {
        mockMvc.perform(post("/api/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new BookBetRequest(
                                        "user-9",
                                        "event-300",
                                        "market-3",
                                        "winner-9",
                                        new java.math.BigDecimal("50.00"),
                                        BetType.PREMIUM
                                )
                        )))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/event-outcomes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new EventOutcomeRequest("event-300", "Match Winner", "winner-9")
                        )))
                .andExpect(status().isAccepted());

        verify(betSettlementMessagePublisher, timeout(5000).atLeastOnce()).publish(any(BetSettlementMessage.class));

        List<Bet> eventBets = betRepository.findByEventIdAndStatus("event-300", BetStatus.SETTLED);
        assertThat(eventBets).hasSize(1);
        assertThat(eventBets.get(0).getUserId()).isEqualTo("user-9");
        assertThat(eventBets.get(0).getBetType()).isEqualTo(BetType.PREMIUM);
    }
}

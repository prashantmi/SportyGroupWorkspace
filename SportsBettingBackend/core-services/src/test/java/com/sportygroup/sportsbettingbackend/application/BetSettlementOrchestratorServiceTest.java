package com.sportygroup.sportsbettingbackend.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sportygroup.sportsbettingbackend.config.AppProperties;
import com.sportygroup.sportsbettingbackend.domain.Bet;
import com.sportygroup.sportsbettingbackend.domain.BetSettlementMessage;
import com.sportygroup.sportsbettingbackend.domain.BetStatus;
import com.sportygroup.sportsbettingbackend.domain.BetType;
import com.sportygroup.sportsbettingbackend.domain.EventOutcomeMessage;
import com.sportygroup.sportsbettingbackend.domain.SettlementResult;
import com.sportygroup.sportsbettingbackend.persistence.BetRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BetSettlementOrchestratorServiceTest {

    @Mock
    private BetRepository betRepository;

    @Mock
    private BetSettlementMessagePublisher betSettlementMessagePublisher;

    @Captor
    private ArgumentCaptor<BetSettlementMessage> settlementCaptor;

    private BetSettlementOrchestratorService service;

    @BeforeEach
    void setUp() {
        AppProperties properties = new AppProperties();
        properties.setPayoutRatios(Map.of(
                BetType.STANDARD, new BigDecimal("1.0"),
                BetType.BOOSTED, new BigDecimal("1.5"),
                BetType.PREMIUM, new BigDecimal("2.0")
        ));
        service = new BetSettlementOrchestratorService(
                betRepository,
                new PayoutCalculator(properties),
                betSettlementMessagePublisher
        );
    }

    @Test
    void shouldPublishOneSettlementMessagePerMatchedOpenBet() {
        when(betRepository.findByEventIdAndStatus("event-100", BetStatus.OPEN)).thenReturn(List.of(
                new Bet(1L, "user-1", "event-100", "market-1", "winner-1",
                        new BigDecimal("100.00"), BetType.STANDARD, BetStatus.OPEN),
                new Bet(2L, "user-2", "event-100", "market-1", "winner-2",
                        new BigDecimal("75.00"), BetType.BOOSTED, BetStatus.OPEN)
        ));

        service.process(new EventOutcomeMessage("event-100", "Match Winner", "winner-1"));

        org.mockito.Mockito.verify(betSettlementMessagePublisher, org.mockito.Mockito.times(2))
                .publish(settlementCaptor.capture());

        List<BetSettlementMessage> settlements = settlementCaptor.getAllValues();
        assertThat(settlements).hasSize(2);
        assertThat(settlements.get(0).result()).isEqualTo(SettlementResult.WIN);
        assertThat(settlements.get(0).payoutAmount()).isEqualByComparingTo("100.00");
        assertThat(settlements.get(1).result()).isEqualTo(SettlementResult.LOSS);
        assertThat(settlements.get(1).payoutAmount()).isEqualByComparingTo("0.00");
    }

    @Test
    void shouldDoNothingWhenThereAreNoMatchingOpenBets() {
        when(betRepository.findByEventIdAndStatus("event-404", BetStatus.OPEN)).thenReturn(List.of());

        service.process(new EventOutcomeMessage("event-404", "No Bets", "winner-1"));

        verifyNoInteractions(betSettlementMessagePublisher);
    }
}

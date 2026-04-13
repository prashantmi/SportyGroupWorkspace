package com.sportygroup.sportsbettingbackend.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.sportygroup.sportsbettingbackend.config.AppProperties;
import com.sportygroup.sportsbettingbackend.domain.Bet;
import com.sportygroup.sportsbettingbackend.domain.BetStatus;
import com.sportygroup.sportsbettingbackend.domain.BetType;
import com.sportygroup.sportsbettingbackend.domain.EventOutcomeMessage;
import com.sportygroup.sportsbettingbackend.domain.SettlementDecision;
import com.sportygroup.sportsbettingbackend.domain.SettlementResult;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PayoutCalculatorTest {

    private PayoutCalculator payoutCalculator;

    @BeforeEach
    void setUp() {
        AppProperties appProperties = new AppProperties();
        appProperties.setPayoutRatios(Map.of(
                BetType.STANDARD, new BigDecimal("1.0"),
                BetType.BOOSTED, new BigDecimal("1.5"),
                BetType.PREMIUM, new BigDecimal("2.0")
        ));
        payoutCalculator = new PayoutCalculator(appProperties);
    }

    @Test
    void shouldReturnConfiguredPayoutForWinningStandardBet() {
        Bet bet = createBet(BetType.STANDARD, "winner-1", "100.00");

        SettlementDecision decision = payoutCalculator.determineSettlement(
                bet,
                new EventOutcomeMessage("event-100", "Match Winner", "winner-1")
        );

        assertThat(decision.result()).isEqualTo(SettlementResult.WIN);
        assertThat(decision.payoutAmount()).isEqualByComparingTo("100.00");
    }

    @Test
    void shouldReturnConfiguredPayoutForWinningBoostedBet() {
        Bet bet = createBet(BetType.BOOSTED, "winner-1", "100.00");

        SettlementDecision decision = payoutCalculator.determineSettlement(
                bet,
                new EventOutcomeMessage("event-100", "Match Winner", "winner-1")
        );

        assertThat(decision.result()).isEqualTo(SettlementResult.WIN);
        assertThat(decision.payoutAmount()).isEqualByComparingTo("150.00");
    }

    @Test
    void shouldReturnConfiguredPayoutForWinningPremiumBet() {
        Bet bet = createBet(BetType.PREMIUM, "winner-1", "100.00");

        SettlementDecision decision = payoutCalculator.determineSettlement(
                bet,
                new EventOutcomeMessage("event-100", "Match Winner", "winner-1")
        );

        assertThat(decision.result()).isEqualTo(SettlementResult.WIN);
        assertThat(decision.payoutAmount()).isEqualByComparingTo("200.00");
    }

    @Test
    void shouldReturnZeroPayoutForLosingBet() {
        Bet bet = createBet(BetType.PREMIUM, "winner-2", "100.00");

        SettlementDecision decision = payoutCalculator.determineSettlement(
                bet,
                new EventOutcomeMessage("event-100", "Match Winner", "winner-1")
        );

        assertThat(decision.result()).isEqualTo(SettlementResult.LOSS);
        assertThat(decision.payoutAmount()).isEqualByComparingTo("0.00");
    }

    private Bet createBet(BetType betType, String winnerId, String amount) {
        return new Bet(
                1L,
                "user-1",
                "event-100",
                "market-1",
                winnerId,
                new BigDecimal(amount),
                betType,
                BetStatus.OPEN
        );
    }
}

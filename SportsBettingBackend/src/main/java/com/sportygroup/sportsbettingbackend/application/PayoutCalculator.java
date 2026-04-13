package com.sportygroup.sportsbettingbackend.application;

import com.sportygroup.sportsbettingbackend.config.AppProperties;
import com.sportygroup.sportsbettingbackend.domain.Bet;
import com.sportygroup.sportsbettingbackend.domain.BetType;
import com.sportygroup.sportsbettingbackend.domain.EventOutcomeMessage;
import com.sportygroup.sportsbettingbackend.domain.SettlementDecision;
import com.sportygroup.sportsbettingbackend.domain.SettlementResult;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PayoutCalculator {

    private final Map<BetType, BigDecimal> payoutRatios;

    public PayoutCalculator(AppProperties appProperties) {
        this.payoutRatios = appProperties.getPayoutRatios();
    }

    public SettlementDecision determineSettlement(Bet bet, EventOutcomeMessage outcome) {
        boolean isWin = bet.getEventWinnerId().equals(outcome.eventWinnerId());
        if (!isWin) {
            return new SettlementDecision(SettlementResult.LOSS, BigDecimal.ZERO.setScale(2));
        }

        BigDecimal ratio = payoutRatios.get(bet.getBetType());
        if (ratio == null) {
            throw new IllegalStateException("Missing payout ratio for bet type " + bet.getBetType());
        }

        BigDecimal payout = bet.getBetAmount()
                .multiply(ratio)
                .setScale(2, RoundingMode.HALF_UP);
        return new SettlementDecision(SettlementResult.WIN, payout);
    }
}

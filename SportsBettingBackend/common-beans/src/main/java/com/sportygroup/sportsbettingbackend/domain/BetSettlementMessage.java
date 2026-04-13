package com.sportygroup.sportsbettingbackend.domain;

import java.math.BigDecimal;

public record BetSettlementMessage(
        Long betId,
        String userId,
        String eventId,
        String eventName,
        BetType betType,
        String selectedWinnerId,
        String actualWinnerId,
        BigDecimal betAmount,
        SettlementResult result,
        BigDecimal payoutAmount
) {
}

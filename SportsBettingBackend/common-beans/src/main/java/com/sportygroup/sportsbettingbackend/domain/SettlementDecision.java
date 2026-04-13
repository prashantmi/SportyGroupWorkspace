package com.sportygroup.sportsbettingbackend.domain;

import java.math.BigDecimal;

public record SettlementDecision(
        SettlementResult result,
        BigDecimal payoutAmount
) {
}

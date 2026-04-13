package com.sportygroup.sportsbettingbackend.model;

import com.sportygroup.sportsbettingbackend.domain.BetStatus;
import com.sportygroup.sportsbettingbackend.domain.BetType;
import java.math.BigDecimal;

public record BookBetResponse(
        Long betId,
        String userId,
        String eventId,
        String eventMarketId,
        String eventWinnerId,
        BigDecimal betAmount,
        BetType betType,
        BetStatus status
) {
}

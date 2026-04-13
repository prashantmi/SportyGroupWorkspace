package com.sportygroup.sportsbettingbackend.model;

import com.sportygroup.sportsbettingbackend.domain.BetType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record BookBetRequest(
        @NotBlank String userId,
        @NotBlank String eventId,
        @NotBlank String eventMarketId,
        @NotBlank String eventWinnerId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal betAmount,
        @NotNull BetType betType
) {
}

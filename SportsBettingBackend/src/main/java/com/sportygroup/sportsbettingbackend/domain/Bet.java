package com.sportygroup.sportsbettingbackend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "bets")
public class Bet {

    @Id
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String eventId;

    @Column(nullable = false)
    private String eventMarketId;

    @Column(nullable = false)
    private String eventWinnerId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal betAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BetType betType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BetStatus status;

    @Enumerated(EnumType.STRING)
    private SettlementResult result;

    @Column(precision = 19, scale = 2)
    private BigDecimal payoutAmount;

    private Instant settledAt;

    protected Bet() {
    }

    public Bet(
            Long id,
            String userId,
            String eventId,
            String eventMarketId,
            String eventWinnerId,
            BigDecimal betAmount,
            BetType betType,
            BetStatus status
    ) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.eventId = Objects.requireNonNull(eventId);
        this.eventMarketId = Objects.requireNonNull(eventMarketId);
        this.eventWinnerId = Objects.requireNonNull(eventWinnerId);
        this.betAmount = Objects.requireNonNull(betAmount);
        this.betType = Objects.requireNonNull(betType);
        this.status = Objects.requireNonNull(status);
    }

    public void markSettled(SettlementResult result, BigDecimal payoutAmount, Instant settledAt) {
        this.result = Objects.requireNonNull(result);
        this.payoutAmount = Objects.requireNonNull(payoutAmount);
        this.settledAt = Objects.requireNonNull(settledAt);
        this.status = BetStatus.SETTLED;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventMarketId() {
        return eventMarketId;
    }

    public String getEventWinnerId() {
        return eventWinnerId;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public BetType getBetType() {
        return betType;
    }

    public BetStatus getStatus() {
        return status;
    }

    public SettlementResult getResult() {
        return result;
    }

    public BigDecimal getPayoutAmount() {
        return payoutAmount;
    }

    public Instant getSettledAt() {
        return settledAt;
    }
}

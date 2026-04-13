package com.sportygroup.sportsbettingbackend.application;

import com.sportygroup.sportsbettingbackend.domain.Bet;
import com.sportygroup.sportsbettingbackend.domain.BetSettlementMessage;
import com.sportygroup.sportsbettingbackend.domain.BetStatus;
import com.sportygroup.sportsbettingbackend.domain.EventOutcomeMessage;
import com.sportygroup.sportsbettingbackend.domain.SettlementDecision;
import com.sportygroup.sportsbettingbackend.persistence.BetRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BetSettlementOrchestratorService {

    private static final Logger log = LoggerFactory.getLogger(BetSettlementOrchestratorService.class);

    private final BetRepository betRepository;
    private final PayoutCalculator payoutCalculator;
    private final BetSettlementMessagePublisher betSettlementMessagePublisher;

    public BetSettlementOrchestratorService(
            BetRepository betRepository,
            PayoutCalculator payoutCalculator,
            BetSettlementMessagePublisher betSettlementMessagePublisher
    ) {
        this.betRepository = betRepository;
        this.payoutCalculator = payoutCalculator;
        this.betSettlementMessagePublisher = betSettlementMessagePublisher;
    }

    public void process(EventOutcomeMessage outcome) {
        List<Bet> betsToSettle = betRepository.findByEventIdAndStatus(outcome.eventId(), BetStatus.OPEN);
        if (betsToSettle.isEmpty()) {
            log.info("No open bets found for event {}", outcome.eventId());
            return;
        }

        for (Bet bet : betsToSettle) {
            SettlementDecision decision = payoutCalculator.determineSettlement(bet, outcome);
            BetSettlementMessage settlementMessage = new BetSettlementMessage(
                    bet.getId(),
                    bet.getUserId(),
                    bet.getEventId(),
                    outcome.eventName(),
                    bet.getBetType(),
                    bet.getEventWinnerId(),
                    outcome.eventWinnerId(),
                    bet.getBetAmount(),
                    decision.result(),
                    decision.payoutAmount()
            );
            log.info("Publishing bet settlement to RocketMQ: {}", settlementMessage);
            betSettlementMessagePublisher.publish(settlementMessage);
        }
    }
}

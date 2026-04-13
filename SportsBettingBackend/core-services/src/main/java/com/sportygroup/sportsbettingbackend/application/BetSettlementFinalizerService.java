package com.sportygroup.sportsbettingbackend.application;

import com.sportygroup.sportsbettingbackend.domain.Bet;
import com.sportygroup.sportsbettingbackend.domain.BetSettlementMessage;
import com.sportygroup.sportsbettingbackend.domain.BetStatus;
import com.sportygroup.sportsbettingbackend.persistence.BetRepository;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BetSettlementFinalizerService {

    private static final Logger log = LoggerFactory.getLogger(BetSettlementFinalizerService.class);

    private final BetRepository betRepository;

    public BetSettlementFinalizerService(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    @Transactional
    public void finalizeSettlement(BetSettlementMessage settlementMessage) {
        Bet bet = betRepository.findById(settlementMessage.betId())
                .orElse(null);

        if (bet == null) {
            log.warn("Received settlement for unknown bet {}", settlementMessage.betId());
            return;
        }

        if (bet.getStatus() == BetStatus.SETTLED) {
            log.info("Bet {} already settled, ignoring duplicate message", settlementMessage.betId());
            return;
        }

        bet.markSettled(settlementMessage.result(), settlementMessage.payoutAmount(), Instant.now());
        betRepository.save(bet);
    }
}

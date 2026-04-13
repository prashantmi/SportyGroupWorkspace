package com.sportygroup.sportsbettingbackend.application;

import com.sportygroup.sportsbettingbackend.domain.Bet;
import com.sportygroup.sportsbettingbackend.domain.BetStatus;
import com.sportygroup.sportsbettingbackend.domain.BetType;
import com.sportygroup.sportsbettingbackend.persistence.BetRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BetBookingService {

    private static final long INITIAL_BET_ID = 1001L;

    private final BetRepository betRepository;

    public BetBookingService(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    @Transactional
    public synchronized Bet book(
            String userId,
            String eventId,
            String eventMarketId,
            String eventWinnerId,
            BigDecimal betAmount,
            BetType betType
    ) {
        Bet bet = new Bet(
                nextBetId(),
                userId,
                eventId,
                eventMarketId,
                eventWinnerId,
                betAmount,
                betType,
                BetStatus.OPEN
        );
        return betRepository.save(bet);
    }

    private Long nextBetId() {
        return betRepository.findTopByOrderByIdDesc()
                .map(existingBet -> existingBet.getId() + 1)
                .orElse(INITIAL_BET_ID);
    }
}

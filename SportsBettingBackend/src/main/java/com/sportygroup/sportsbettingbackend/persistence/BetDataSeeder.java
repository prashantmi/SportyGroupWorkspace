package com.sportygroup.sportsbettingbackend.persistence;

import com.sportygroup.sportsbettingbackend.domain.Bet;
import com.sportygroup.sportsbettingbackend.domain.BetStatus;
import com.sportygroup.sportsbettingbackend.domain.BetType;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class BetDataSeeder implements ApplicationRunner {

    private final BetRepository betRepository;

    public BetDataSeeder(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (betRepository.count() > 0) {
            return;
        }

        betRepository.saveAll(List.of(
                new Bet(1001L, "user-1", "event-100", "market-1", "winner-1",
                        new BigDecimal("100.00"), BetType.STANDARD, BetStatus.OPEN),
                new Bet(1002L, "user-2", "event-100", "market-1", "winner-2",
                        new BigDecimal("75.00"), BetType.BOOSTED, BetStatus.OPEN),
                new Bet(1003L, "user-3", "event-100", "market-2", "winner-1",
                        new BigDecimal("40.00"), BetType.PREMIUM, BetStatus.OPEN),
                new Bet(1004L, "user-4", "event-200", "market-1", "winner-3",
                        new BigDecimal("20.00"), BetType.STANDARD, BetStatus.OPEN)
        ));
    }
}

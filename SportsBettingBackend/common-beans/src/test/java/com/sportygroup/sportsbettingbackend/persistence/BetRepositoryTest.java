package com.sportygroup.sportsbettingbackend.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.sportygroup.sportsbettingbackend.domain.Bet;
import com.sportygroup.sportsbettingbackend.domain.BetStatus;
import com.sportygroup.sportsbettingbackend.domain.BetType;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BetRepositoryTest {

    @Autowired
    private BetRepository betRepository;

    @Test
    void shouldFindOnlyMatchingOpenBetsByEventId() {
        betRepository.saveAll(List.of(
                new Bet(1L, "user-1", "event-100", "market-1", "winner-1",
                        new BigDecimal("25.00"), BetType.STANDARD, BetStatus.OPEN),
                new Bet(2L, "user-2", "event-100", "market-1", "winner-2",
                        new BigDecimal("30.00"), BetType.BOOSTED, BetStatus.SETTLED),
                new Bet(3L, "user-3", "event-200", "market-1", "winner-3",
                        new BigDecimal("45.00"), BetType.PREMIUM, BetStatus.OPEN)
        ));

        List<Bet> bets = betRepository.findByEventIdAndStatus("event-100", BetStatus.OPEN);

        assertThat(bets)
                .extracting(Bet::getId)
                .containsExactly(1L);
    }
}

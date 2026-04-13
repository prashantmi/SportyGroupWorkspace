package com.sportygroup.sportsbettingbackend.messaging.rocketmq;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportygroup.sportsbettingbackend.application.BetSettlementFinalizerService;
import com.sportygroup.sportsbettingbackend.domain.Bet;
import com.sportygroup.sportsbettingbackend.domain.BetStatus;
import com.sportygroup.sportsbettingbackend.domain.BetType;
import com.sportygroup.sportsbettingbackend.domain.BetSettlementMessage;
import com.sportygroup.sportsbettingbackend.domain.SettlementResult;
import com.sportygroup.sportsbettingbackend.persistence.BetRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(BetSettlementFinalizerService.class)
class RocketMqSettlementConsumerTest {

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private BetSettlementFinalizerService betSettlementFinalizerService;

    @Test
    void shouldConsumeSettlementAndMarkBetAsSettled() throws Exception {
        betRepository.save(new Bet(
                1001L,
                "user-1",
                "event-100",
                "market-1",
                "winner-1",
                new BigDecimal("100.00"),
                BetType.STANDARD,
                BetStatus.OPEN
        ));

        RocketMqSettlementConsumer consumer = new RocketMqSettlementConsumer(
                new ObjectMapper(),
                betSettlementFinalizerService
        );

        consumer.consume(new ObjectMapper().writeValueAsString(new BetSettlementMessage(
                1001L,
                "user-1",
                "event-100",
                "Match Winner",
                BetType.STANDARD,
                "winner-1",
                "winner-1",
                new BigDecimal("100.00"),
                SettlementResult.WIN,
                new BigDecimal("100.00")
        )));

        Bet bet = betRepository.findById(1001L).orElseThrow();
        assertThat(bet.getStatus()).isEqualTo(BetStatus.SETTLED);
        assertThat(bet.getResult()).isEqualTo(SettlementResult.WIN);
        assertThat(bet.getPayoutAmount()).isEqualByComparingTo("100.00");
        assertThat(bet.getSettledAt()).isNotNull();
    }
}

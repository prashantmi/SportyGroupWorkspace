package com.sportygroup.sportsbettingbackend.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.sportygroup.sportsbettingbackend.domain.Bet;
import com.sportygroup.sportsbettingbackend.domain.BetStatus;
import com.sportygroup.sportsbettingbackend.domain.BetType;
import com.sportygroup.sportsbettingbackend.persistence.BetRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BetBookingServiceTest {

    @Mock
    private BetRepository betRepository;

    private BetBookingService service;

    @BeforeEach
    void setUp() {
        service = new BetBookingService(betRepository);
    }

    @Test
    void shouldBookOpenBetWithNextAvailableId() {
        when(betRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(
                new Bet(1004L, "user-4", "event-200", "market-1", "winner-3",
                        new BigDecimal("20.00"), BetType.STANDARD, BetStatus.OPEN)
        ));
        when(betRepository.save(any(Bet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bet bet = service.book(
                "user-5",
                "event-300",
                "market-3",
                "winner-9",
                new BigDecimal("55.00"),
                BetType.BOOSTED
        );

        assertThat(bet.getId()).isEqualTo(1005L);
        assertThat(bet.getStatus()).isEqualTo(BetStatus.OPEN);
        assertThat(bet.getUserId()).isEqualTo("user-5");
        assertThat(bet.getEventId()).isEqualTo("event-300");
        assertThat(bet.getEventMarketId()).isEqualTo("market-3");
        assertThat(bet.getEventWinnerId()).isEqualTo("winner-9");
        assertThat(bet.getBetAmount()).isEqualByComparingTo("55.00");
        assertThat(bet.getBetType()).isEqualTo(BetType.BOOSTED);
    }

    @Test
    void shouldStartFromInitialBetIdWhenRepositoryIsEmpty() {
        when(betRepository.findTopByOrderByIdDesc()).thenReturn(Optional.empty());
        when(betRepository.save(any(Bet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bet bet = service.book(
                "user-1",
                "event-100",
                "market-1",
                "winner-1",
                new BigDecimal("10.00"),
                BetType.STANDARD
        );

        assertThat(bet.getId()).isEqualTo(1001L);
    }
}

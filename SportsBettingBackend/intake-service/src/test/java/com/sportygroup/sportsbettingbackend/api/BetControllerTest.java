package com.sportygroup.sportsbettingbackend.api;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportygroup.sportsbettingbackend.application.BetBookingService;
import com.sportygroup.sportsbettingbackend.domain.Bet;
import com.sportygroup.sportsbettingbackend.domain.BetStatus;
import com.sportygroup.sportsbettingbackend.domain.BetType;
import com.sportygroup.sportsbettingbackend.model.BookBetRequest;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BetController.class)
class BetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BetBookingService betBookingService;

    @Test
    void shouldBookBet() throws Exception {
        BookBetRequest request = new BookBetRequest(
                "user-9",
                "event-300",
                "market-3",
                "winner-9",
                new BigDecimal("50.00"),
                BetType.PREMIUM
        );
        given(betBookingService.book(
                "user-9",
                "event-300",
                "market-3",
                "winner-9",
                new BigDecimal("50.00"),
                BetType.PREMIUM
        )).willReturn(new Bet(
                1005L,
                "user-9",
                "event-300",
                "market-3",
                "winner-9",
                new BigDecimal("50.00"),
                BetType.PREMIUM,
                BetStatus.OPEN
        ));

        mockMvc.perform(post("/api/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.betId").value(1005L))
                .andExpect(jsonPath("$.userId").value("user-9"))
                .andExpect(jsonPath("$.eventId").value("event-300"))
                .andExpect(jsonPath("$.eventMarketId").value("market-3"))
                .andExpect(jsonPath("$.eventWinnerId").value("winner-9"))
                .andExpect(jsonPath("$.betAmount").value(50.00))
                .andExpect(jsonPath("$.betType").value("PREMIUM"))
                .andExpect(jsonPath("$.status").value("OPEN"));

        then(betBookingService).should().book(
                "user-9",
                "event-300",
                "market-3",
                "winner-9",
                new BigDecimal("50.00"),
                BetType.PREMIUM
        );
    }

    @Test
    void shouldRejectInvalidBetPayload() throws Exception {
        BookBetRequest request = new BookBetRequest(
                "",
                "event-300",
                "",
                " ",
                new BigDecimal("0.00"),
                null
        );

        mockMvc.perform(post("/api/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

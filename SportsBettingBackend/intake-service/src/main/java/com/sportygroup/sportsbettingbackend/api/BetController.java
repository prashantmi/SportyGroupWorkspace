package com.sportygroup.sportsbettingbackend.api;

import com.sportygroup.sportsbettingbackend.application.BetBookingService;
import com.sportygroup.sportsbettingbackend.domain.Bet;
import com.sportygroup.sportsbettingbackend.model.BookBetRequest;
import com.sportygroup.sportsbettingbackend.model.BookBetResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bets")
public class BetController {

    private final BetBookingService betBookingService;

    public BetController(BetBookingService betBookingService) {
        this.betBookingService = betBookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookBetResponse book(@Valid @RequestBody BookBetRequest request) {
        Bet bet = betBookingService.book(
                request.userId(),
                request.eventId(),
                request.eventMarketId(),
                request.eventWinnerId(),
                request.betAmount(),
                request.betType()
        );
        return new BookBetResponse(
                bet.getId(),
                bet.getUserId(),
                bet.getEventId(),
                bet.getEventMarketId(),
                bet.getEventWinnerId(),
                bet.getBetAmount(),
                bet.getBetType(),
                bet.getStatus()
        );
    }
}

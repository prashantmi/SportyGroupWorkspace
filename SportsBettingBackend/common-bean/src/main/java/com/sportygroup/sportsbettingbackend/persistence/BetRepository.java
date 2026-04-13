package com.sportygroup.sportsbettingbackend.persistence;

import com.sportygroup.sportsbettingbackend.domain.Bet;
import com.sportygroup.sportsbettingbackend.domain.BetStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByEventIdAndStatus(String eventId, BetStatus status);
}

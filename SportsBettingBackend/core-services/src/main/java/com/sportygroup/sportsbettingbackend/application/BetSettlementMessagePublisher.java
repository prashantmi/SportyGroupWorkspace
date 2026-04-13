package com.sportygroup.sportsbettingbackend.application;

import com.sportygroup.sportsbettingbackend.domain.BetSettlementMessage;

public interface BetSettlementMessagePublisher {

    void publish(BetSettlementMessage settlementMessage);
}

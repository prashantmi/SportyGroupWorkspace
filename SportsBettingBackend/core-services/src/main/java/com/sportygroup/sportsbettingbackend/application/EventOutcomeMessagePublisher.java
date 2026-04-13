package com.sportygroup.sportsbettingbackend.application;

import com.sportygroup.sportsbettingbackend.domain.EventOutcomeMessage;

public interface EventOutcomeMessagePublisher {

    void publish(EventOutcomeMessage message);
}

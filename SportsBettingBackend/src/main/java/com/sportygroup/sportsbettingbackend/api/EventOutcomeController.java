package com.sportygroup.sportsbettingbackend.api;

import com.sportygroup.sportsbettingbackend.application.EventOutcomePublisherService;
import com.sportygroup.sportsbettingbackend.config.AppProperties;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event-outcomes")
public class EventOutcomeController {

    private final EventOutcomePublisherService eventOutcomePublisherService;
    private final AppProperties appProperties;

    public EventOutcomeController(
            EventOutcomePublisherService eventOutcomePublisherService,
            AppProperties appProperties
    ) {
        this.eventOutcomePublisherService = eventOutcomePublisherService;
        this.appProperties = appProperties;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PublishEventOutcomeResponse publish(@Valid @RequestBody EventOutcomeRequest request) {
        eventOutcomePublisherService.publish(request);
        return new PublishEventOutcomeResponse(
                request.eventId(),
                "PUBLISHED",
                appProperties.getKafka().getEventOutcomesTopic()
        );
    }
}

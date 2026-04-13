package com.sportygroup.sportsbettingbackend.api;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportygroup.sportsbettingbackend.application.EventOutcomePublisherService;
import com.sportygroup.sportsbettingbackend.config.AppProperties;
import com.sportygroup.sportsbettingbackend.domain.EventOutcomeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EventOutcomeController.class)
class EventOutcomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventOutcomePublisherService eventOutcomePublisherService;

    @MockitoBean
    private AppProperties appProperties;

    @BeforeEach
    void setUp() {
        AppProperties.Kafka kafka = new AppProperties.Kafka();
        kafka.setEventOutcomesTopic("event-outcomes");
        given(appProperties.getKafka()).willReturn(kafka);
    }

    @Test
    void shouldPublishEventOutcome() throws Exception {
        EventOutcomeRequest request = new EventOutcomeRequest("event-100", "Match Winner", "winner-1");
        given(eventOutcomePublisherService.publish("event-100", "Match Winner", "winner-1"))
                .willReturn(new EventOutcomeMessage("event-100", "Match Winner", "winner-1"));

        mockMvc.perform(post("/api/event-outcomes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.eventId").value("event-100"))
                .andExpect(jsonPath("$.status").value("PUBLISHED"))
                .andExpect(jsonPath("$.topic").value("event-outcomes"));

        then(eventOutcomePublisherService).should().publish("event-100", "Match Winner", "winner-1");
    }

    @Test
    void shouldRejectInvalidPayload() throws Exception {
        EventOutcomeRequest request = new EventOutcomeRequest("", " ", null);

        mockMvc.perform(post("/api/event-outcomes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

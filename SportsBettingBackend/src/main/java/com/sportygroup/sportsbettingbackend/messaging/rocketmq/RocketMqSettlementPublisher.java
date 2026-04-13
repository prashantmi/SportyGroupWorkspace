package com.sportygroup.sportsbettingbackend.messaging.rocketmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportygroup.sportsbettingbackend.config.AppProperties;
import com.sportygroup.sportsbettingbackend.domain.BetSettlementMessage;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

@Component
public class RocketMqSettlementPublisher {

    private final RocketMQTemplate rocketMqTemplate;
    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;

    public RocketMqSettlementPublisher(
            RocketMQTemplate rocketMqTemplate,
            ObjectMapper objectMapper,
            AppProperties appProperties
    ) {
        this.rocketMqTemplate = rocketMqTemplate;
        this.objectMapper = objectMapper;
        this.appProperties = appProperties;
    }

    public void publish(BetSettlementMessage settlementMessage) {
        rocketMqTemplate.convertAndSend(
                appProperties.getRocketmq().getBetSettlementsTopic(),
                writeValue(settlementMessage)
        );
    }

    private String writeValue(BetSettlementMessage settlementMessage) {
        try {
            return objectMapper.writeValueAsString(settlementMessage);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize bet settlement", exception);
        }
    }
}

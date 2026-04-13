package com.sportygroup.sportsbettingbackend.messaging.rocketmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportygroup.sportsbettingbackend.application.BetSettlementFinalizerService;
import com.sportygroup.sportsbettingbackend.domain.BetSettlementMessage;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
        topic = "${app.rocketmq.bet-settlements-topic}",
        consumerGroup = "${app.rocketmq.consumer-group}",
        consumeMode = ConsumeMode.CONCURRENTLY,
        messageModel = MessageModel.CLUSTERING
)
public class RocketMqSettlementConsumer implements RocketMQListener<String> {

    private static final Logger log = LoggerFactory.getLogger(RocketMqSettlementConsumer.class);

    private final ObjectMapper objectMapper;
    private final BetSettlementFinalizerService betSettlementFinalizerService;

    public RocketMqSettlementConsumer(
            ObjectMapper objectMapper,
            BetSettlementFinalizerService betSettlementFinalizerService
    ) {
        this.objectMapper = objectMapper;
        this.betSettlementFinalizerService = betSettlementFinalizerService;
    }

    @Override
    public void onMessage(String payload) {
        consume(payload);
    }

    public void consume(String payload) {
        BetSettlementMessage settlementMessage = readValue(payload);
        log.info("Consumed RocketMQ bet settlement message: {}", settlementMessage);
        betSettlementFinalizerService.finalizeSettlement(settlementMessage);
    }

    private BetSettlementMessage readValue(String payload) {
        try {
            return objectMapper.readValue(payload, BetSettlementMessage.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to deserialize bet settlement message", exception);
        }
    }
}

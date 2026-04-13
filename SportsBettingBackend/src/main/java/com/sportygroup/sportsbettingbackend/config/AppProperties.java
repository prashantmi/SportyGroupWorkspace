package com.sportygroup.sportsbettingbackend.config;

import com.sportygroup.sportsbettingbackend.domain.BetType;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Kafka kafka = new Kafka();
    private final Rocketmq rocketmq = new Rocketmq();
    private Map<BetType, BigDecimal> payoutRatios = new EnumMap<>(BetType.class);

    public Kafka getKafka() {
        return kafka;
    }

    public Rocketmq getRocketmq() {
        return rocketmq;
    }

    public Map<BetType, BigDecimal> getPayoutRatios() {
        return payoutRatios;
    }

    public void setPayoutRatios(Map<BetType, BigDecimal> payoutRatios) {
        this.payoutRatios = payoutRatios;
    }

    public static class Kafka {
        private String eventOutcomesTopic;

        public String getEventOutcomesTopic() {
            return eventOutcomesTopic;
        }

        public void setEventOutcomesTopic(String eventOutcomesTopic) {
            this.eventOutcomesTopic = eventOutcomesTopic;
        }
    }

    public static class Rocketmq {
        private String betSettlementsTopic;
        private String consumerGroup;

        public String getBetSettlementsTopic() {
            return betSettlementsTopic;
        }

        public void setBetSettlementsTopic(String betSettlementsTopic) {
            this.betSettlementsTopic = betSettlementsTopic;
        }

        public String getConsumerGroup() {
            return consumerGroup;
        }

        public void setConsumerGroup(String consumerGroup) {
            this.consumerGroup = consumerGroup;
        }
    }
}

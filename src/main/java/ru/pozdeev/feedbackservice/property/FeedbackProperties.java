package ru.pozdeev.feedbackservice.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Класс для работы с конфигурационными параметрами, связанными с фидбеком.
 */
@Component
@ConfigurationProperties(prefix = "feedback")
@Getter
@Setter
public class FeedbackProperties {

    /**
     * Количество дней, после которых отправленные опросы считаются истекшими.
     */
    private int sentSurveyExpiryDays;

    /**
     * Количество дней, после которых неотправленные опросы считаются истекшими.
     */
    private int unsentSurveyExpiryDays;

    /**
     * Конфигурация Kafka.
     */
    private KafkaProperties kafka;

    @Getter
    @Setter
    public static class KafkaProperties {

        private ProducerProperties producer;

        private ConsumerProperties consumer;
    }

    @Getter
    @Setter
    public static class ProducerProperties {

        private ProducerTopicProperties dailyMetrics;
    }

    @Getter
    @Setter
    public static class ConsumerProperties {

        private String groupId;

        private TopicProperties customerEvents;
    }

    @Getter
    @Setter
    public static class TopicProperties {

        private boolean enabled;

        private String topic;
    }

    @Getter
    @Setter
    public static class ProducerTopicProperties extends TopicProperties {

        private int sendingTimeoutSec;
    }
}

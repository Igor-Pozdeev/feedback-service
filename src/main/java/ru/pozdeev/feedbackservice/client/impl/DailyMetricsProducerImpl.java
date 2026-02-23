package ru.pozdeev.feedbackservice.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.pozdeev.feedbackservice.client.DailyMetricsProducer;
import ru.pozdeev.feedbackservice.dto.event.DailyMetricsEvent;
import ru.pozdeev.feedbackservice.property.FeedbackProperties;

import java.util.concurrent.TimeUnit;

/**
 * Реализация продюсера для отправки событий агрегированных метрик в Kafka
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyMetricsProducerImpl implements DailyMetricsProducer {

    private final KafkaTemplate<String, DailyMetricsEvent> kafkaTemplate;
    private final FeedbackProperties feedbackProperties;

    @Override
    public void send(DailyMetricsEvent event) {
        String topic = feedbackProperties.getKafka().getProducer().getDailyMetrics().getTopic();
        int timeout = feedbackProperties.getKafka().getProducer().getDailyMetrics().getSendingTimeoutSec();

        log.info("Sending daily metrics event to Kafka topic: {}, event: {}", topic, event);

        try {
            kafkaTemplate.send(topic, event.campaignId().toString(), event)
                    .get(timeout, TimeUnit.SECONDS);

            log.info("Successfully sent daily metrics event for campaign: {}", event.campaignId());
        } catch (Exception e) {
            log.error("Failed to send daily metrics event for campaign: {}. Error: {}", event.campaignId(), e.getMessage());
            // We don't throw exception here to not break the whole aggregation job
        }
    }
}

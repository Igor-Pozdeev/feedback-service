package ru.pozdeev.feedbackservice.client.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import ru.pozdeev.feedbackservice.dto.event.DailyMetricsEvent;
import ru.pozdeev.feedbackservice.property.FeedbackProperties;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для DailyMetricsProducerImpl")
class DailyMetricsProducerImplTest {

    @Mock
    private KafkaTemplate<String, DailyMetricsEvent> kafkaTemplate;
    @Mock
    private FeedbackProperties feedbackProperties;

    @InjectMocks
    private DailyMetricsProducerImpl dailyMetricsProducer;

    private DailyMetricsEvent event;
    private String topic = "daily-metrics.V1";
    private int timeout = 5;

    @BeforeEach
    void setUp() {
        event = new DailyMetricsEvent(
                "DAILY_METRICS_AGGREGATED",
                UUID.randomUUID(),
                "Campaign",
                LocalDate.now(),
                "NPS",
                100,
                new BigDecimal("45.5"),
                null,
                null,
                null,
                LocalDateTime.now()
        );

        FeedbackProperties.KafkaProperties kafkaProps = mock(FeedbackProperties.KafkaProperties.class);
        FeedbackProperties.ProducerProperties producerProps = mock(FeedbackProperties.ProducerProperties.class);
        FeedbackProperties.ProducerTopicProperties topicProps = mock(FeedbackProperties.ProducerTopicProperties.class);

        when(feedbackProperties.getKafka()).thenReturn(kafkaProps);
        when(kafkaProps.getProducer()).thenReturn(producerProps);
        when(producerProps.getDailyMetrics()).thenReturn(topicProps);
        when(topicProps.getTopic()).thenReturn(topic);
        when(topicProps.getSendingTimeoutSec()).thenReturn(timeout);
    }

    @Test
    @DisplayName("Успешная отправка события в Kafka")
    void send_whenSuccess_thenSendToKafka() throws Exception {
        // Arrange
        CompletableFuture<SendResult<String, DailyMetricsEvent>> future = CompletableFuture.completedFuture(mock(SendResult.class));
        when(kafkaTemplate.send(eq(topic), eq(event.campaignId().toString()), eq(event))).thenReturn(future);

        // Act
        dailyMetricsProducer.send(event);

        // Assert
        verify(kafkaTemplate).send(eq(topic), eq(event.campaignId().toString()), eq(event));
    }

    @Test
    @DisplayName("Обработка ошибки при отправке в Kafka")
    void send_whenKafkaThrowsException_thenHandleError() {
        // Arrange
        doThrow(new RuntimeException("Kafka error")).when(kafkaTemplate).send(anyString(), anyString(), any(DailyMetricsEvent.class));

        // Act & Assert (exception should not be rethrown)
        dailyMetricsProducer.send(event);

        verify(kafkaTemplate).send(eq(topic), eq(event.campaignId().toString()), eq(event));
    }

    @Test
    @DisplayName("Обработка таймаута при отправке в Kafka")
    void send_whenKafkaTimeout_thenHandleError() throws Exception {
        // Arrange
        CompletableFuture<SendResult<String, DailyMetricsEvent>> future = mock(CompletableFuture.class);
        when(kafkaTemplate.send(eq(topic), eq(event.campaignId().toString()), eq(event))).thenReturn(future);
        when(future.get(timeout, TimeUnit.SECONDS)).thenThrow(new RuntimeException("Timeout"));

        // Act & Assert
        dailyMetricsProducer.send(event);

        verify(kafkaTemplate).send(eq(topic), eq(event.campaignId().toString()), eq(event));
    }
}

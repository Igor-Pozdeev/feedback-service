package ru.pozdeev.feedbackservice.listener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import ru.pozdeev.feedbackservice.common.AbstractTest;
import ru.pozdeev.feedbackservice.service.SurveyCreationService;

@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class CustomerEventListenerTest extends AbstractTest {

    @Autowired
    private SurveyCreationService surveyCreationService;

    @Value("${feedback.kafka.consumer.customer-events.topic}")
    private String topic;

    @Test
    void listen_shouldCallService_whenValidEventIsReceived() {
        String eventJson = """
                            {
                                "event_type":"ORDER_COMPLETED",
                                "guid":"cust_123",
                                "context_id":"order_456",
                                "context_type":"ORDER",
                                "timestamp":"2024-01-15T14:30:00Z",
                                "metadata":{}
                            }""";

        kafkaTemplate.send(topic, eventJson);
    }

    @Test
    void listen_shouldHandleServiceException() {
        String eventJson = """
                            {
                                "event_type":"ORDER_COMPLETED",
                                "guid":"cust_123",
                                "context_id":"order_456",
                                "context_type":"ORDER",
                                "timestamp":"2024-01-15T14:30:00Z",
                                "metadata":{}
                            }""";

        kafkaTemplate.send(topic, eventJson);
    }
}

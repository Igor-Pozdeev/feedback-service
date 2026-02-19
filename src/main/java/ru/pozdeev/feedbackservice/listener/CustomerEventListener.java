package ru.pozdeev.feedbackservice.listener;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.pozdeev.feedbackservice.dto.event.CustomerEvent;
import ru.pozdeev.feedbackservice.service.SurveyCreationService;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "feedback.kafka.consumer.customer-events.enabled", havingValue = "true")
public class CustomerEventListener {

    private final SurveyCreationService surveyCreationService;

    /**
     * Слушатель событий от клиентов из Kafka.
     *
     * @param event событие клиента
     */
    @KafkaListener(
            topics = "${feedback.kafka.consumer.customer-events.topic}",
            groupId = "${feedback.kafka.consumer.group-id}"
            // ПРИМЕЧАНИЕ: Предполагается, что ConcurrentKafkaListenerContainerFactory настроен
            // с DefaultErrorHandler и поддержкой десериализации JSON.
    )
    public void handle(@Payload @Valid CustomerEvent event) {
        log.info("Получено событие от клиента: {}", event);
        try {
            surveyCreationService.processCustomerEvent(event);
        } catch (Exception e) {
            log.error("Ошибка обработки события от клиента с guid '{}' и contextId '{}'",
                    event.guid(), event.contextId(), e);
            // Повторно выбрасываем исключение для обработки настроенным обработчиком ошибок (например, DefaultErrorHandler),
            // который затем должен переместить сообщение в Dead Letter Topic (DLT).
            throw e;
        }
    }
}

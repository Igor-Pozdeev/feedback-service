package ru.pozdeev.feedbackservice.client;

import ru.pozdeev.feedbackservice.dto.event.DailyMetricsEvent;

/**
 * Продюсер для отправки событий агрегированных метрик в Kafka
 */
public interface DailyMetricsProducer {

    /**
     * Отправляет событие с агрегированными метриками в Kafka
     *
     * @param event событие для отправки
     */
    void send(DailyMetricsEvent event);
}

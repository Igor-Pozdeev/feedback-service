package ru.pozdeev.feedbackservice.service;

import java.time.LocalDate;

/**
 * Сервис для агрегации метрик по кампаниям за определенную дату
 */
public interface MetricsAggregationService {

    /**
     * Агрегирует метрики за указанную дату, сохраняет их в базу данных и отправляет события в Kafka
     *
     * @param date дата, за которую необходимо агрегировать метрики
     */
    void aggregateMetricsForDate(LocalDate date);
}

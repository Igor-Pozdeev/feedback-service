package ru.pozdeev.feedbackservice.service;

import ru.pozdeev.feedbackservice.dto.FilterMetricsRequest;
import ru.pozdeev.feedbackservice.dto.FilterMetricsResponse;

/**
 * Сервис для работы с метриками
 */
public interface MetricsService {

    /**
     * Получение отфильтрованных метрик с расчетом сводной статистики
     *
     * @param request параметры фильтрации
     * @return отфильтрованные метрики и сводная статистика
     */
    FilterMetricsResponse getFilteredMetrics(FilterMetricsRequest request);
}

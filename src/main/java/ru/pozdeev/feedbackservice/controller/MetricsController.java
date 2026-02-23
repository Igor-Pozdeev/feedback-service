package ru.pozdeev.feedbackservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pozdeev.feedbackservice.dto.FilterMetricsRequest;
import ru.pozdeev.feedbackservice.dto.FilterMetricsResponse;
import ru.pozdeev.feedbackservice.dto.common.CommonResponse;
import ru.pozdeev.feedbackservice.service.MetricsService;

/**
 * Контроллер для работы с метриками
 */
@Validated
@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
@Tag(name = "metrics", description = "API для получения метрик")
public class MetricsController {

    private final MetricsService metricsService;

    /**
     * Получение отфильтрованных метрик
     *
     * @param request параметры фильтрации
     * @return список метрик и сводная статистика
     */
    @PostMapping("/filter")
    @Operation(summary = "Получить отфильтрованные метрики", description = "Возвращает агрегированные ежедневные метрики с возможностью фильтрации")
    public CommonResponse<FilterMetricsResponse> getFilteredMetrics(@RequestBody @Valid FilterMetricsRequest request) {
        FilterMetricsResponse response = metricsService.getFilteredMetrics(request);

        return CommonResponse.ok(response);
    }
}

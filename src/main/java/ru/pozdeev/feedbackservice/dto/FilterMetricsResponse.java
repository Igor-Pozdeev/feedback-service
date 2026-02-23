package ru.pozdeev.feedbackservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Ответ с отфильтрованными метриками и сводкой.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с отфильтрованными метриками и сводкой")
public class FilterMetricsResponse {

    @NotEmpty
    @Schema(description = "Список ежедневных метрик")
    private List<MetricResponse> metrics;

    @NotNull
    @Schema(description = "Сводная статистика за период")
    private MetricsSummaryResponse summary;
}

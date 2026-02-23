package ru.pozdeev.feedbackservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Сводная статистика за период.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Сводная статистика за период")
public class MetricsSummaryResponse {

    @NotNull
    @Schema(description = "Общее количество ответов за период", example = "4500")
    private Integer totalResponses;

    @NotNull
    @Schema(description = "Среднее значение метрики за период", example = "42.3")
    private BigDecimal averageScore;

    @NotNull
    @Schema(description = "Дата начала периода", example = "2024-01-01")
    private LocalDate periodFrom;

    @NotNull
    @Schema(description = "Дата окончания периода", example = "2024-01-31")
    private LocalDate periodTo;
}

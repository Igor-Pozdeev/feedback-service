package ru.pozdeev.feedbackservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Запрос на фильтрацию метрик.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на фильтрацию метрик")
public class FilterMetricsRequest {

    @Schema(description = "Идентификатор кампании", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID campaignId;

    @Schema(description = "Тип опроса (например, NPS, CSAT)", example = "NPS")
    private String type;

    @Schema(description = "Дата начала периода", example = "2024-01-01")
    private LocalDate dateFrom;

    @Schema(description = "Дата окончания периода", example = "2024-01-31")
    private LocalDate dateTo;
}

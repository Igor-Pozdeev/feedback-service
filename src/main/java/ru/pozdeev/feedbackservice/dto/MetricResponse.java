package ru.pozdeev.feedbackservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Метрика за конкретную дату для кампании.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Метрика за конкретную дату для кампании")
public class MetricResponse {

    @NotNull
    @Schema(description = "Идентификатор кампании", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID campaignId;

    @NotNull
    @Schema(description = "Наименование кампании", example = "NPS после заказа")
    private String campaignName;

    @NotNull
    @Schema(description = "Дата метрики", example = "2024-01-15")
    private LocalDate date;

    @NotNull
    @Schema(description = "Тип метрики", example = "NPS")
    private String type;

    @NotNull
    @Schema(description = "Общее количество ответов", example = "150")
    private Integer totalResponses;

    @NotNull
    @Schema(description = "Значение метрики (например, NPS score или CSAT %)", example = "45.5")
    private BigDecimal scoreValue;

    @Schema(description = "Количество промоутеров (только для NPS)", example = "100")
    private Integer promoters;

    @Schema(description = "Количество пассивных (только для NPS)", example = "30")
    private Integer passives;

    @Schema(description = "Количество детракторов (только для NPS)", example = "20")
    private Integer detractors;
}

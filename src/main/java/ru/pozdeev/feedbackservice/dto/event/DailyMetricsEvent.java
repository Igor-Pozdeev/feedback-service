package ru.pozdeev.feedbackservice.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Событие с агрегированными ежедневными метриками
 */
public record DailyMetricsEvent(
        @NotBlank
        String eventType,

        @NotNull
        UUID campaignId,

        @NotBlank
        String campaignName,

        @NotNull
        LocalDate date,

        @NotBlank
        String type,

        @NotNull
        Integer totalResponses,

        @NotNull
        BigDecimal scoreValue,

        Integer promoters,

        Integer passives,

        Integer detractors,

        @NotNull
        LocalDateTime timestamp
) {
}

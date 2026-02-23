package ru.pozdeev.feedbackservice.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Событие с агрегированными ежедневными метриками
 *
 * @param eventType тип события
 * @param campaignId идентификатор кампании
 * @param campaignName название кампании
 * @param date дата метрик
 * @param type тип опроса
 * @param totalResponses общее количество ответов
 * @param scoreValue значение метрики
 * @param promoters количество промоутеров
 * @param passives количество пассивных
 * @param detractors количество детракторов
 * @param timestamp время агрегации
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

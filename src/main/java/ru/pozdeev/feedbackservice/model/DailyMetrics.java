package ru.pozdeev.feedbackservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Агрегированные ежедневные метрики
 */
@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "daily_metrics")
public class DailyMetrics extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Кампания, для которой собрана метрика
     */
    private UUID campaignId;

    /**
     * Дата, за которую собрана метрика
     */
    private LocalDateTime date;

    /**
     * Тип опроса
     */
    private String surveyTypeCode;

    /**
     * Общее количество ответов
     */
    private Integer totalResponses;

    /**
     * Значение метрики (NPS: -100..100, CSAT: 0..100, CES: 1..5)
     */
    private BigDecimal scoreValue;

    /**
     * Количество "промоутеров" (только для NPS)
     */
    private Integer promoters;

    /**
     * Количество "пассивных" (только для NPS)
     */
    private Integer passives;

    /**
     * Количество "детракторов" (только для NPS)
     */
    private Integer detractors;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DailyMetrics that = (DailyMetrics) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

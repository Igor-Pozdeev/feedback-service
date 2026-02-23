package ru.pozdeev.feedbackservice.repository.specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ru.pozdeev.feedbackservice.model.DailyMetrics;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Спецификация для динамической фильтрации ежедневных метрик
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DailyMetricsSpecification {

    /**
     * Фильтрация по идентификатору кампании
     *
     * @param campaignId идентификатор кампании
     * @return спецификация
     */
    public static Specification<DailyMetrics> hasCampaignId(UUID campaignId) {
        return (root, query, criteriaBuilder) -> {
            if (campaignId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("campaignId"), campaignId);
        };
    }

    /**
     * Фильтрация по коду типа опроса
     *
     * @param surveyTypeCode код типа опроса
     * @return спецификация
     */
    public static Specification<DailyMetrics> hasSurveyTypeCode(String surveyTypeCode) {
        return (root, query, criteriaBuilder) -> {
            if (surveyTypeCode == null || surveyTypeCode.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("surveyTypeCode"), surveyTypeCode);
        };
    }

    /**
     * Фильтрация по дате (от)
     *
     * @param dateFrom начальная дата
     * @return спецификация
     */
    public static Specification<DailyMetrics> hasDateFrom(LocalDate dateFrom) {
        return (root, query, criteriaBuilder) -> {
            if (dateFrom == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThanOrEqualTo(root.get("date"), dateFrom.atStartOfDay());
        };
    }

    /**
     * Фильтрация по дате (до)
     *
     * @param dateTo конечная дата
     * @return спецификация
     */
    public static Specification<DailyMetrics> hasDateTo(LocalDate dateTo) {
        return (root, query, criteriaBuilder) -> {
            if (dateTo == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.lessThanOrEqualTo(root.get("date"), dateTo.atTime(LocalTime.MAX));
        };
    }
}

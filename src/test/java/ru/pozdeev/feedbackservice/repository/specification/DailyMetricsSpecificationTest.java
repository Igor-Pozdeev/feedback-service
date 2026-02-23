package ru.pozdeev.feedbackservice.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import ru.pozdeev.feedbackservice.model.DailyMetrics;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тесты для спецификации DailyMetricsSpecification
 */
@ExtendWith(MockitoExtension.class)
class DailyMetricsSpecificationTest {

    @Mock
    private Root<DailyMetrics> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<Object> objectPath;

    @Mock
    private Path<LocalDateTime> datePath;

    @Test
    @DisplayName("Фильтрация по идентификатору кампании")
    void hasCampaignId_should_add_equal_predicate() {
        when(root.get("campaignId")).thenReturn(objectPath);
        UUID campaignId = UUID.randomUUID();
        Specification<DailyMetrics> spec = DailyMetricsSpecification.hasCampaignId(campaignId);

        spec.toPredicate(root, query, criteriaBuilder);

        verify(root).get("campaignId");
        verify(criteriaBuilder).equal(objectPath, campaignId);
    }

    @Test
    @DisplayName("Фильтрация по коду типа опроса")
    void hasSurveyTypeCode_should_add_equal_predicate() {
        when(root.get("surveyTypeCode")).thenReturn(objectPath);
        String typeCode = "NPS";
        Specification<DailyMetrics> spec = DailyMetricsSpecification.hasSurveyTypeCode(typeCode);

        spec.toPredicate(root, query, criteriaBuilder);

        verify(root).get("surveyTypeCode");
        verify(criteriaBuilder).equal(objectPath, typeCode);
    }

    @Test
    @DisplayName("Фильтрация по дате от")
    void hasDateFrom_should_add_greaterThanOrEqualTo_predicate() {
        when(root.<LocalDateTime>get("date")).thenReturn(datePath);
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        Specification<DailyMetrics> spec = DailyMetricsSpecification.hasDateFrom(dateFrom);

        spec.toPredicate(root, query, criteriaBuilder);

        verify(root).get("date");
        verify(criteriaBuilder).greaterThanOrEqualTo(eq(datePath), eq(dateFrom.atStartOfDay()));
    }

    @Test
    @DisplayName("Фильтрация по дате до")
    void hasDateTo_should_add_lessThanOrEqualTo_predicate() {
        when(root.<LocalDateTime>get("date")).thenReturn(datePath);
        LocalDate dateTo = LocalDate.of(2024, 1, 31);
        Specification<DailyMetrics> spec = DailyMetricsSpecification.hasDateTo(dateTo);

        spec.toPredicate(root, query, criteriaBuilder);

        verify(root).get("date");
        verify(criteriaBuilder).lessThanOrEqualTo(eq(datePath), eq(dateTo.atTime(LocalTime.MAX)));
    }
}

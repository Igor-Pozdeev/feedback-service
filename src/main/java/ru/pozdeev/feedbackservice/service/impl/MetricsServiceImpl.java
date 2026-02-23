package ru.pozdeev.feedbackservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pozdeev.feedbackservice.dto.FilterMetricsRequest;
import ru.pozdeev.feedbackservice.dto.FilterMetricsResponse;
import ru.pozdeev.feedbackservice.dto.MetricResponse;
import ru.pozdeev.feedbackservice.dto.MetricsSummaryResponse;
import ru.pozdeev.feedbackservice.mapper.MetricsMapper;
import ru.pozdeev.feedbackservice.model.Campaign;
import ru.pozdeev.feedbackservice.model.DailyMetrics;
import ru.pozdeev.feedbackservice.repository.CampaignRepository;
import ru.pozdeev.feedbackservice.repository.DailyMetricsRepository;
import ru.pozdeev.feedbackservice.repository.specification.DailyMetricsSpecification;
import ru.pozdeev.feedbackservice.service.MetricsService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с метриками
 */
@Service
@RequiredArgsConstructor
public class MetricsServiceImpl implements MetricsService {

    private final DailyMetricsRepository dailyMetricsRepository;

    private final CampaignRepository campaignRepository;

    private final MetricsMapper metricsMapper;

    @Override
    @Transactional(readOnly = true)
    public FilterMetricsResponse getFilteredMetrics(FilterMetricsRequest request) {
        Specification<DailyMetrics> spec = buildSpecification(request);

        List<DailyMetrics> metrics = dailyMetricsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "date"));

        Map<UUID, String> campaignNames = getCampaignNames(metrics);

        List<MetricResponse> metricResponses = metrics.stream()
                .map(m -> metricsMapper.toMetricResponse(m, campaignNames.getOrDefault(m.getCampaignId(), "Unknown")))
                .toList();

        MetricsSummaryResponse summary = calculateSummary(metrics, request);

        return FilterMetricsResponse.builder()
                .metrics(metricResponses)
                .summary(summary)
                .build();
    }

    private Specification<DailyMetrics> buildSpecification(FilterMetricsRequest request) {
        return Specification.where(DailyMetricsSpecification.hasCampaignId(request.getCampaignId()))
                .and(DailyMetricsSpecification.hasSurveyTypeCode(request.getType()))
                .and(DailyMetricsSpecification.hasDateFrom(request.getDateFrom()))
                .and(DailyMetricsSpecification.hasDateTo(request.getDateTo()));
    }

    private Map<UUID, String> getCampaignNames(List<DailyMetrics> metrics) {
        List<UUID> campaignIds = metrics.stream()
                .map(DailyMetrics::getCampaignId)
                .distinct()
                .toList();

        return campaignRepository.findAllById(campaignIds).stream()
                .collect(Collectors.toMap(Campaign::getId, Campaign::getName));
    }

    private MetricsSummaryResponse calculateSummary(List<DailyMetrics> metrics, FilterMetricsRequest request) {
        int totalResponses = metrics.stream()
                .mapToInt(DailyMetrics::getTotalResponses)
                .sum();

        BigDecimal averageScore = calculateAverageScore(metrics);
        LocalDate[] period = calculatePeriod(metrics, request);

        return MetricsSummaryResponse.builder()
                .totalResponses(totalResponses)
                .averageScore(averageScore)
                .periodFrom(period[0])
                .periodTo(period[1])
                .build();
    }

    private BigDecimal calculateAverageScore(List<DailyMetrics> metrics) {
        if (metrics.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sumScore = metrics.stream()
                .map(DailyMetrics::getScoreValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sumScore.divide(BigDecimal.valueOf(metrics.size()), 2, RoundingMode.HALF_UP);
    }

    private LocalDate[] calculatePeriod(List<DailyMetrics> metrics, FilterMetricsRequest request) {
        LocalDate periodFrom = request.getDateFrom();
        LocalDate periodTo = request.getDateTo();

        if (periodFrom == null && !metrics.isEmpty()) {
            periodFrom = metrics.stream()
                    .map(m -> m.getDate().toLocalDate())
                    .min(LocalDate::compareTo)
                    .orElse(null);
        }

        if (periodTo == null && !metrics.isEmpty()) {
            periodTo = metrics.stream()
                    .map(m -> m.getDate().toLocalDate())
                    .max(LocalDate::compareTo)
                    .orElse(null);
        }

        // Если дат нет ни в запросе, ни в данных, используем текущую дату, чтобы избежать null
        if (periodFrom == null) {
            periodFrom = LocalDate.now();
        }

        if (periodTo == null) {
            periodTo = LocalDate.now();
        }

        return new LocalDate[]{periodFrom, periodTo};
    }
}

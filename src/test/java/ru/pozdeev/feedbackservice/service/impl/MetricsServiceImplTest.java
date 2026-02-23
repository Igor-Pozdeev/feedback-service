package ru.pozdeev.feedbackservice.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.pozdeev.feedbackservice.dto.FilterMetricsRequest;
import ru.pozdeev.feedbackservice.dto.FilterMetricsResponse;
import ru.pozdeev.feedbackservice.dto.MetricResponse;
import ru.pozdeev.feedbackservice.mapper.MetricsMapper;
import ru.pozdeev.feedbackservice.model.Campaign;
import ru.pozdeev.feedbackservice.model.DailyMetrics;
import ru.pozdeev.feedbackservice.repository.CampaignRepository;
import ru.pozdeev.feedbackservice.repository.DailyMetricsRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тесты для MetricsServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class MetricsServiceImplTest {

    @Mock
    private DailyMetricsRepository dailyMetricsRepository;

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private MetricsMapper metricsMapper;

    @InjectMocks
    private MetricsServiceImpl metricsService;

    @Test
    @DisplayName("Получение отфильтрованных метрик: успешный поиск и расчет summary")
    void getFilteredMetrics_should_return_response_with_summary() {
        UUID campaignId = UUID.randomUUID();
        String campaignName = "Test Campaign";
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        LocalDate dateTo = LocalDate.of(2024, 1, 31);

        FilterMetricsRequest request = FilterMetricsRequest.builder()
                .campaignId(campaignId)
                .type("NPS")
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .build();

        DailyMetrics dailyMetrics = DailyMetrics.builder()
                .campaignId(campaignId)
                .date(LocalDateTime.of(2024, 1, 15, 10, 0))
                .surveyTypeCode("NPS")
                .totalResponses(100)
                .scoreValue(new BigDecimal("45.5"))
                .build();

        Campaign campaign = Campaign.builder()
                .id(campaignId)
                .name(campaignName)
                .build();

        MetricResponse metricResponse = MetricResponse.builder()
                .campaignId(campaignId)
                .campaignName(campaignName)
                .date(LocalDate.of(2024, 1, 15))
                .type("NPS")
                .totalResponses(100)
                .scoreValue(new BigDecimal("45.5"))
                .build();

        when(dailyMetricsRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(dailyMetrics));
        when(campaignRepository.findAllById(anyList()))
                .thenReturn(List.of(campaign));
        when(metricsMapper.toMetricResponse(eq(dailyMetrics), eq(campaignName)))
                .thenReturn(metricResponse);

        FilterMetricsResponse response = metricsService.getFilteredMetrics(request);

        assertThat(response.getMetrics()).hasSize(1);
        assertThat(response.getMetrics().get(0).getCampaignName()).isEqualTo(campaignName);
        assertThat(response.getSummary().getTotalResponses()).isEqualTo(100);
        assertThat(response.getSummary().getAverageScore()).isEqualTo(new BigDecimal("45.50"));
        assertThat(response.getSummary().getPeriodFrom()).isEqualTo(dateFrom);
        assertThat(response.getSummary().getPeriodTo()).isEqualTo(dateTo);

        verify(dailyMetricsRepository).findAll(any(Specification.class), eq(Sort.by(Sort.Direction.DESC, "date")));
    }

    @Test
    @DisplayName("Расчет summary: пустой список метрик (возврат текущей даты)")
    void calculateSummary_empty_metrics_should_return_now_dates() {
        FilterMetricsRequest request = FilterMetricsRequest.builder().build();
        LocalDate now = LocalDate.now();

        when(dailyMetricsRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(Collections.emptyList());
        when(campaignRepository.findAllById(anyList()))
                .thenReturn(Collections.emptyList());

        FilterMetricsResponse response = metricsService.getFilteredMetrics(request);

        assertThat(response.getMetrics()).isEmpty();
        assertThat(response.getSummary().getTotalResponses()).isZero();
        assertThat(response.getSummary().getAverageScore()).isEqualTo(BigDecimal.ZERO);
        assertThat(response.getSummary().getPeriodFrom()).isEqualTo(now);
        assertThat(response.getSummary().getPeriodTo()).isEqualTo(now);
    }

    @Test
    @DisplayName("Расчет summary: приоритет дат из запроса")
    void calculateSummary_request_dates_should_have_priority() {
        UUID campaignId = UUID.randomUUID();
        LocalDate requestFrom = LocalDate.of(2024, 1, 1);
        LocalDate requestTo = LocalDate.of(2024, 1, 31);
        LocalDate metricDate = LocalDate.of(2024, 1, 15);

        FilterMetricsRequest request = FilterMetricsRequest.builder()
                .campaignId(campaignId)
                .dateFrom(requestFrom)
                .dateTo(requestTo)
                .build();

        DailyMetrics dailyMetrics = DailyMetrics.builder()
                .campaignId(campaignId)
                .date(metricDate.atStartOfDay())
                .totalResponses(10)
                .scoreValue(new BigDecimal("50.00"))
                .build();

        when(dailyMetricsRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(dailyMetrics));
        when(campaignRepository.findAllById(anyList()))
                .thenReturn(Collections.emptyList());

        FilterMetricsResponse response = metricsService.getFilteredMetrics(request);

        assertThat(response.getSummary().getPeriodFrom()).isEqualTo(requestFrom);
        assertThat(response.getSummary().getPeriodTo()).isEqualTo(requestTo);
    }
}

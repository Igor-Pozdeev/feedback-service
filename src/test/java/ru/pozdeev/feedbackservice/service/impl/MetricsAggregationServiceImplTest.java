package ru.pozdeev.feedbackservice.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pozdeev.feedbackservice.client.DailyMetricsProducer;
import ru.pozdeev.feedbackservice.mapper.MetricsMapper;
import ru.pozdeev.feedbackservice.model.Campaign;
import ru.pozdeev.feedbackservice.model.CustomerFeedback;
import ru.pozdeev.feedbackservice.model.DailyMetrics;
import ru.pozdeev.feedbackservice.model.Survey;
import ru.pozdeev.feedbackservice.repository.CampaignRepository;
import ru.pozdeev.feedbackservice.repository.CustomerFeedbackRepository;
import ru.pozdeev.feedbackservice.repository.DailyMetricsRepository;
import ru.pozdeev.feedbackservice.repository.SurveyRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для MetricsAggregationServiceImpl")
class MetricsAggregationServiceImplTest {

    @Mock
    private CustomerFeedbackRepository customerFeedbackRepository;
    @Mock
    private SurveyRepository surveyRepository;
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private DailyMetricsRepository dailyMetricsRepository;
    @Mock
    private DailyMetricsProducer dailyMetricsProducer;
    @Mock
    private MetricsMapper metricsMapper;

    @InjectMocks
    private MetricsAggregationServiceImpl metricsAggregationService;

    @Test
    @DisplayName("Агрегация NPS метрик: расчет и отправка события")
    void aggregateMetricsForDate_whenNps_thenCalculateAndSend() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 2, 22);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        UUID campaignId = UUID.randomUUID();
        UUID surveyId1 = UUID.randomUUID();
        UUID surveyId2 = UUID.randomUUID();
        UUID surveyId3 = UUID.randomUUID();

        CustomerFeedback f1 = CustomerFeedback.builder().surveyId(surveyId1).score(10).build(); // Promoter
        CustomerFeedback f2 = CustomerFeedback.builder().surveyId(surveyId2).score(7).build();  // Passive
        CustomerFeedback f3 = CustomerFeedback.builder().surveyId(surveyId3).score(0).build();  // Detractor
        List<CustomerFeedback> feedbacks = List.of(f1, f2, f3);

        Survey s1 = Survey.builder().id(surveyId1).campaignId(campaignId).build();
        Survey s2 = Survey.builder().id(surveyId2).campaignId(campaignId).build();
        Survey s3 = Survey.builder().id(surveyId3).campaignId(campaignId).build();

        Campaign campaign = Campaign.builder()
                .id(campaignId)
                .name("NPS Campaign")
                .surveyTypeCode("NPS")
                .build();

        when(customerFeedbackRepository.findByCreateTimeBetween(startOfDay, endOfDay)).thenReturn(feedbacks);
        when(surveyRepository.findAllById(any())).thenReturn(List.of(s1, s2, s3));
        when(campaignRepository.findAllById(any())).thenReturn(List.of(campaign));
        when(dailyMetricsRepository.findByCampaignIdAndDate(campaignId, startOfDay)).thenReturn(Optional.empty());

        // Act
        metricsAggregationService.aggregateMetricsForDate(date);

        // Assert
        ArgumentCaptor<DailyMetrics> metricsCaptor = ArgumentCaptor.forClass(DailyMetrics.class);
        verify(dailyMetricsRepository).save(metricsCaptor.capture());
        DailyMetrics savedMetrics = metricsCaptor.getValue();

        assertThat(savedMetrics.getCampaignId()).isEqualTo(campaignId);
        assertThat(savedMetrics.getPromoters()).isEqualTo(1);
        assertThat(savedMetrics.getPassives()).isEqualTo(1);
        assertThat(savedMetrics.getDetractors()).isEqualTo(1);
        // NPS = (1 - 1) / 3 * 100 = 0
        assertThat(savedMetrics.getScoreValue()).isEqualByComparingTo(BigDecimal.ZERO);

        verify(metricsMapper).toDailyMetricsEvent(eq(savedMetrics), eq("NPS Campaign"), any());
        verify(dailyMetricsProducer).send(any());
    }

    @Test
    @DisplayName("Агрегация CSAT метрик")
    void aggregateMetricsForDate_whenCsat_thenCalculate() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 2, 22);
        UUID campaignId = UUID.randomUUID();
        UUID surveyId = UUID.randomUUID();

        CustomerFeedback f1 = CustomerFeedback.builder().surveyId(surveyId).score(5).build(); // Satisfied
        CustomerFeedback f2 = CustomerFeedback.builder().surveyId(surveyId).score(3).build(); // Not satisfied
        List<CustomerFeedback> feedbacks = List.of(f1, f2);

        Survey s = Survey.builder().id(surveyId).campaignId(campaignId).build();
        Campaign campaign = Campaign.builder().id(campaignId).surveyTypeCode("CSAT").name("CSAT Campaign").build();

        when(customerFeedbackRepository.findByCreateTimeBetween(any(), any())).thenReturn(feedbacks);
        when(surveyRepository.findAllById(any())).thenReturn(List.of(s));
        when(campaignRepository.findAllById(any())).thenReturn(List.of(campaign));

        // Act
        metricsAggregationService.aggregateMetricsForDate(date);

        // Assert
        ArgumentCaptor<DailyMetrics> metricsCaptor = ArgumentCaptor.forClass(DailyMetrics.class);
        verify(dailyMetricsRepository).save(metricsCaptor.capture());
        DailyMetrics savedMetrics = metricsCaptor.getValue();

        // CSAT = 1 / 2 * 100 = 50
        assertThat(savedMetrics.getScoreValue()).isEqualByComparingTo(new BigDecimal("50.0000"));
    }

    @Test
    @DisplayName("Агрегация CES метрик")
    void aggregateMetricsForDate_whenCes_thenCalculate() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 2, 22);
        UUID campaignId = UUID.randomUUID();
        UUID surveyId = UUID.randomUUID();

        CustomerFeedback f1 = CustomerFeedback.builder().surveyId(surveyId).score(5).build();
        CustomerFeedback f2 = CustomerFeedback.builder().surveyId(surveyId).score(4).build();
        List<CustomerFeedback> feedbacks = List.of(f1, f2);

        Survey s = Survey.builder().id(surveyId).campaignId(campaignId).build();
        Campaign campaign = Campaign.builder().id(campaignId).surveyTypeCode("CES").name("CES Campaign").build();

        when(customerFeedbackRepository.findByCreateTimeBetween(any(), any())).thenReturn(feedbacks);
        when(surveyRepository.findAllById(any())).thenReturn(List.of(s));
        when(campaignRepository.findAllById(any())).thenReturn(List.of(campaign));

        // Act
        metricsAggregationService.aggregateMetricsForDate(date);

        // Assert
        ArgumentCaptor<DailyMetrics> metricsCaptor = ArgumentCaptor.forClass(DailyMetrics.class);
        verify(dailyMetricsRepository).save(metricsCaptor.capture());
        DailyMetrics savedMetrics = metricsCaptor.getValue();

        // CES = (5 + 4) / 2 = 4.5
        assertThat(savedMetrics.getScoreValue()).isEqualByComparingTo(new BigDecimal("4.50"));
    }

    @Test
    @DisplayName("Агрегация при отсутствии отзывов")
    void aggregateMetricsForDate_whenNoFeedbacks_thenDoNothing() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 2, 22);
        when(customerFeedbackRepository.findByCreateTimeBetween(any(), any())).thenReturn(Collections.emptyList());

        // Act
        metricsAggregationService.aggregateMetricsForDate(date);

        // Assert
        verify(surveyRepository, never()).findAllById(any());
        verify(dailyMetricsRepository, never()).save(any());
    }

    @Test
    @DisplayName("Обновление существующей метрики с сохранением аудит-полей")
    void aggregateMetricsForDate_whenMetricsExist_thenPreserveAuditFields() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 2, 22);
        UUID campaignId = UUID.randomUUID();
        UUID surveyId = UUID.randomUUID();
        UUID existingMetricsId = UUID.randomUUID();
        LocalDateTime existingCreateTime = LocalDateTime.now().minusDays(1);
        String existingCreateUser = "original_user";

        CustomerFeedback f = CustomerFeedback.builder().surveyId(surveyId).score(5).build();
        Survey s = Survey.builder().id(surveyId).campaignId(campaignId).build();
        Campaign campaign = Campaign.builder().id(campaignId).surveyTypeCode("CSAT").name("CSAT Campaign").build();
        DailyMetrics existingMetrics = DailyMetrics.builder()
                .id(existingMetricsId)
                .createTime(existingCreateTime)
                .createUser(existingCreateUser)
                .build();

        when(customerFeedbackRepository.findByCreateTimeBetween(any(), any())).thenReturn(List.of(f));
        when(surveyRepository.findAllById(any())).thenReturn(List.of(s));
        when(campaignRepository.findAllById(any())).thenReturn(List.of(campaign));
        when(dailyMetricsRepository.findByCampaignIdAndDate(eq(campaignId), any())).thenReturn(Optional.of(existingMetrics));

        // Act
        metricsAggregationService.aggregateMetricsForDate(date);

        // Assert
        ArgumentCaptor<DailyMetrics> metricsCaptor = ArgumentCaptor.forClass(DailyMetrics.class);
        verify(dailyMetricsRepository).save(metricsCaptor.capture());
        DailyMetrics savedMetrics = metricsCaptor.getValue();
        
        assertThat(savedMetrics.getId()).isEqualTo(existingMetricsId);
        assertThat(savedMetrics.getCreateTime()).isEqualTo(existingCreateTime);
        assertThat(savedMetrics.getCreateUser()).isEqualTo(existingCreateUser);
    }
}

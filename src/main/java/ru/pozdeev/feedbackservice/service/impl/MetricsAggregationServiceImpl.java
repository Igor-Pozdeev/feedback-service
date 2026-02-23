package ru.pozdeev.feedbackservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pozdeev.feedbackservice.client.DailyMetricsProducer;
import ru.pozdeev.feedbackservice.dto.event.DailyMetricsEvent;
import ru.pozdeev.feedbackservice.mapper.MetricsMapper;
import ru.pozdeev.feedbackservice.model.Campaign;
import ru.pozdeev.feedbackservice.model.CustomerFeedback;
import ru.pozdeev.feedbackservice.model.DailyMetrics;
import ru.pozdeev.feedbackservice.model.Survey;
import ru.pozdeev.feedbackservice.repository.CampaignRepository;
import ru.pozdeev.feedbackservice.repository.CustomerFeedbackRepository;
import ru.pozdeev.feedbackservice.repository.DailyMetricsRepository;
import ru.pozdeev.feedbackservice.repository.SurveyRepository;
import ru.pozdeev.feedbackservice.service.MetricsAggregationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsAggregationServiceImpl implements MetricsAggregationService {

    private static final int SCALE_4 = 4;
    private static final int SCALE_2 = 2;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final int NPS_PROMOTER_MIN_SCORE = 9;
    private static final int NPS_PASSIVE_MIN_SCORE = 7;
    private static final int CSAT_SATISFIED_MIN_SCORE = 4;

    private final CustomerFeedbackRepository customerFeedbackRepository;
    private final SurveyRepository surveyRepository;
    private final CampaignRepository campaignRepository;
    private final DailyMetricsRepository dailyMetricsRepository;
    private final DailyMetricsProducer dailyMetricsProducer;
    private final MetricsMapper metricsMapper;

    @Override
    @Transactional
    public void aggregateMetricsForDate(LocalDate date) {
        log.info("Starting metrics aggregation for date: {}", date);

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<CustomerFeedback> feedbacks = customerFeedbackRepository.findByCreateTimeBetween(startOfDay, endOfDay);

        if (feedbacks.isEmpty()) {
            log.info("No feedbacks found for date: {}", date);
            return;
        }

        processFeedbacks(feedbacks, date);
        log.info("Finished metrics aggregation for date: {}", date);
    }

    private void processFeedbacks(List<CustomerFeedback> feedbacks, LocalDate date) {
        Map<UUID, List<CustomerFeedback>> feedbacksByCampaign = groupFeedbacksByCampaign(feedbacks);
        Set<UUID> campaignIds = feedbacksByCampaign.keySet();
        Map<UUID, Campaign> campaignsMap = campaignRepository.findAllById(campaignIds).stream()
                .collect(Collectors.toMap(Campaign::getId, Function.identity()));

        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<UUID, List<CustomerFeedback>> entry : feedbacksByCampaign.entrySet()) {
            UUID campaignId = entry.getKey();
            Campaign campaign = campaignsMap.get(campaignId);

            if (campaign == null) {
                log.warn("Campaign not found for id: {}", campaignId);
                continue;
            }

            DailyMetrics metrics = calculateMetrics(campaign, date, entry.getValue());
            saveAndSendMetrics(metrics, campaign.getName(), now);
        }
    }

    private void saveAndSendMetrics(DailyMetrics metrics, String campaignName, LocalDateTime now) {
        dailyMetricsRepository.findByCampaignIdAndDate(metrics.getCampaignId(), metrics.getDate())
                .ifPresent(existing -> metrics.setId(existing.getId()));

        dailyMetricsRepository.save(metrics);
        DailyMetricsEvent event = metricsMapper.toDailyMetricsEvent(metrics, campaignName, now);
        dailyMetricsProducer.send(event);
    }

    private Map<UUID, List<CustomerFeedback>> groupFeedbacksByCampaign(List<CustomerFeedback> feedbacks) {
        Set<UUID> surveyIds = feedbacks.stream()
                .map(CustomerFeedback::getSurveyId)
                .collect(Collectors.toSet());

        Map<UUID, Survey> surveysMap = surveyRepository.findAllById(surveyIds).stream()
                .collect(Collectors.toMap(Survey::getId, Function.identity()));

        return feedbacks.stream()
                .collect(Collectors.groupingBy(f -> surveysMap.get(f.getSurveyId()).getCampaignId()));
    }

    private DailyMetrics calculateMetrics(Campaign campaign, LocalDate date, List<CustomerFeedback> feedbacks) {
        String surveyType = campaign.getSurveyTypeCode();

        DailyMetrics metrics = DailyMetrics.builder()
                .campaignId(campaign.getId())
                .date(date.atStartOfDay())
                .surveyTypeCode(surveyType)
                .totalResponses(feedbacks.size())
                .build();

        if (feedbacks.isEmpty()) {
            metrics.setScoreValue(BigDecimal.ZERO);
            return metrics;
        }

        applyCalculationLogic(metrics, feedbacks, surveyType);
        return metrics;
    }

    private void applyCalculationLogic(DailyMetrics metrics, List<CustomerFeedback> feedbacks, String surveyType) {
        switch (surveyType) {
            case "NPS" -> calculateNps(metrics, feedbacks);
            case "CSAT" -> calculateCsat(metrics, feedbacks);
            case "CES" -> calculateCes(metrics, feedbacks);
            default -> {
                log.warn("Unknown survey type: {}", surveyType);
                metrics.setScoreValue(BigDecimal.ZERO);
            }
        }
    }

    private void calculateNps(DailyMetrics metrics, List<CustomerFeedback> feedbacks) {
        int promoters = 0;
        int passives = 0;
        int detractors = 0;

        for (CustomerFeedback feedback : feedbacks) {
            int score = feedback.getScore();
            if (score >= NPS_PROMOTER_MIN_SCORE) {
                promoters++;
            } else if (score >= NPS_PASSIVE_MIN_SCORE) {
                passives++;
            } else {
                detractors++;
            }
        }

        metrics.setPromoters(promoters);
        metrics.setPassives(passives);
        metrics.setDetractors(detractors);
        metrics.setScoreValue(calculateNpsValue(promoters, detractors, feedbacks.size()));
    }

    private BigDecimal calculateNpsValue(int promoters, int detractors, int total) {
        return BigDecimal.valueOf(promoters)
                .subtract(BigDecimal.valueOf(detractors))
                .divide(BigDecimal.valueOf(total), SCALE_4, RoundingMode.HALF_UP)
                .multiply(HUNDRED);
    }

    private void calculateCsat(DailyMetrics metrics, List<CustomerFeedback> feedbacks) {
        long satisfied = feedbacks.stream()
                .filter(f -> f.getScore() >= CSAT_SATISFIED_MIN_SCORE)
                .count();

        BigDecimal scoreValue = BigDecimal.valueOf(satisfied)
                .divide(BigDecimal.valueOf(feedbacks.size()), SCALE_4, RoundingMode.HALF_UP)
                .multiply(HUNDRED);

        metrics.setScoreValue(scoreValue);
    }

    private void calculateCes(DailyMetrics metrics, List<CustomerFeedback> feedbacks) {
        BigDecimal sum = feedbacks.stream()
                .map(f -> BigDecimal.valueOf(f.getScore()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        metrics.setScoreValue(sum.divide(BigDecimal.valueOf(feedbacks.size()), SCALE_2, RoundingMode.HALF_UP));
    }
}

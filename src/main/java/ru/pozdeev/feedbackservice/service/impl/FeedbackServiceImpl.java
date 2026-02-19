package ru.pozdeev.feedbackservice.service.impl;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pozdeev.feedbackservice.dto.FindSurveysResponse;
import ru.pozdeev.feedbackservice.dto.SubmitFeedbackRequest;
import ru.pozdeev.feedbackservice.dto.SubmitFeedbackResponse;
import ru.pozdeev.feedbackservice.dto.SurveyResponse;
import ru.pozdeev.feedbackservice.exception.BusinessException;
import ru.pozdeev.feedbackservice.exception.NotFoundException;
import ru.pozdeev.feedbackservice.mapper.FeedbackMapper;
import ru.pozdeev.feedbackservice.mapper.SurveyMapper;
import ru.pozdeev.feedbackservice.model.Campaign;
import ru.pozdeev.feedbackservice.model.CustomerFeedback;
import ru.pozdeev.feedbackservice.model.Survey;
import ru.pozdeev.feedbackservice.model.SurveyStatus;
import ru.pozdeev.feedbackservice.model.SurveyType;
import ru.pozdeev.feedbackservice.repository.CampaignRepository;
import ru.pozdeev.feedbackservice.repository.CustomerFeedbackRepository;
import ru.pozdeev.feedbackservice.repository.SurveyRepository;
import ru.pozdeev.feedbackservice.repository.SurveyTypeRepository;
import ru.pozdeev.feedbackservice.service.FeedbackService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final SurveyRepository surveyRepository;
    private final CustomerFeedbackRepository customerFeedbackRepository;
    private final CampaignRepository campaignRepository;
    private final SurveyTypeRepository surveyTypeRepository;
    private final FeedbackMapper feedbackMapper;
    private final SurveyMapper surveyMapper;

    @SuppressWarnings("checkstyle:MethodLength")
    @Override
    @Transactional
    public SubmitFeedbackResponse submitFeedback(SubmitFeedbackRequest request) {
        log.info("Processing feedback submission for surveyId: {}", request.getSurveyId());

        Survey survey = surveyRepository.findById(Objects.requireNonNull(request.getSurveyId(), "Идентификатор опроса не может быть пустым"))
                .orElseThrow(() -> new NotFoundException("Опрос с id=" + request.getSurveyId() + " не найден"));

        if (!SurveyStatus.PENDING.equals(survey.getStatus())) {
            throw new BusinessException("Опрос " + survey.getId() + " не находится в статусе PENDING");
        }

        Campaign campaign = campaignRepository.findById(survey.getCampaignId())
                .orElseThrow(() -> new BusinessException("Не найдена кампания с id=" + survey.getCampaignId()));

        SurveyType surveyType = surveyTypeRepository.findById(campaign.getSurveyTypeCode())
                .orElseThrow(() -> new BusinessException("Не найден тип опроса " + campaign.getSurveyTypeCode()));

        validateScore(request.getScore(), surveyType);

        CustomerFeedback feedback = feedbackMapper.toCustomerFeedback(request, survey);

        customerFeedbackRepository.save(feedback);

        survey.setStatus(SurveyStatus.COMPLETED);
        survey.setCompletedAt(LocalDateTime.now());

        surveyRepository.save(survey);

        log.info("Feedback for surveyId: {} has been successfully submitted with feedbackId: {}",
                request.getSurveyId(), feedback.getId());

        return feedbackMapper.toSubmitFeedbackResponse(feedback);
    }

    @Override
    @Transactional
    public FindSurveysResponse getPendingSurveys(String guid) {
        LocalDateTime now = LocalDateTime.now();

        List<Survey> pendingSurveys = getPendingSurveys(guid, now);

        if (pendingSurveys.isEmpty()) {
            throw new BusinessException("Непройденные опросы для guid=" + guid + " не найдены");
        }
        updateifsentAtIsNull(pendingSurveys, now);

        Map<UUID, Campaign> campaignsById = getCampaignsById(pendingSurveys);
        Map<String, SurveyType> surveyTypesByCode = getSurveyTypes(getSurveyTypeCodes(campaignsById));

        List<ru.pozdeev.feedbackservice.dto.SurveyResponse> surveyResponses = pendingSurveys.stream()
                .map(survey -> getSurveyResponse(survey, campaignsById, surveyTypesByCode))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new FindSurveysResponse(surveyResponses);
    }

    @Nullable
    private SurveyResponse getSurveyResponse(Survey survey, Map<UUID, Campaign> campaignsById, Map<String, SurveyType> surveyTypesByCode) {
        Campaign campaign = campaignsById.get(survey.getCampaignId());
        if (campaign == null) {
            return null;
        }
        SurveyType surveyType = surveyTypesByCode.get(campaign.getSurveyTypeCode());
        if (surveyType == null) {
            return null;
        }
        return surveyMapper.toSurveyResponse(survey, campaign, surveyType);
    }

    @Nonnull
    private Map<String, SurveyType> getSurveyTypes(List<String> surveyTypeCodes) {
        return surveyTypeRepository.findAllById(surveyTypeCodes).stream()
                .collect(Collectors.toMap(SurveyType::getCode, Function.identity()));
    }

    private List<Survey> getPendingSurveys(String guid, LocalDateTime now) {
        return surveyRepository
                .findByGuidAndStatusAndScheduledAtBeforeOrderByScheduledAtDesc(
                        guid, SurveyStatus.PENDING, now
                );
    }

    @Nonnull
    private Map<UUID, Campaign> getCampaignsById(List<Survey> pendingSurveys) {
        List<UUID> campaignIds = pendingSurveys.stream()
                .map(Survey::getCampaignId)
                .distinct()
                .collect(Collectors.toList());
        return campaignRepository.findAllById(campaignIds).stream()
                .collect(Collectors.toMap(Campaign::getId, Function.identity()));
    }

    @Nonnull
    private List<String> getSurveyTypeCodes(Map<UUID, Campaign> campaignsById) {
        List<String> surveyTypeCodes = campaignsById.values().stream()
                .map(Campaign::getSurveyTypeCode)
                .distinct()
                .collect(Collectors.toList());
        return surveyTypeCodes;
    }

    private static void updateifsentAtIsNull(List<Survey> pendingSurveys, LocalDateTime now) {
        // Update sentAt for surveys where it's null
        pendingSurveys.stream()
                .filter(survey -> survey.getSentAt() == null)
                .forEach(survey -> survey.setSentAt(now));
    }

    private void validateScore(Integer score, SurveyType surveyType) {
        if (score == null) {
            throw new BusinessException("Оценка не может быть пустой");
        }
        if (score < surveyType.getScaleMin() || score > surveyType.getScaleMax()) {
            throw new BusinessException("Оценка " + score + " выходит за пределы допустимого диапазона для типа опроса "
                    + surveyType.getCode() + " (" + surveyType.getScaleMin() + "-" + surveyType.getScaleMax() + ")");
        }
    }
}
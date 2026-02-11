package ru.pozdeev.feedbackservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pozdeev.feedbackservice.dto.SubmitFeedbackRequest;
import ru.pozdeev.feedbackservice.dto.SubmitFeedbackResponse;
import ru.pozdeev.feedbackservice.exception.BusinessException;
import ru.pozdeev.feedbackservice.exception.NotFoundException;
import ru.pozdeev.feedbackservice.mapper.FeedbackMapper;
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
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final SurveyRepository surveyRepository;
    private final CustomerFeedbackRepository customerFeedbackRepository;
    private final CampaignRepository campaignRepository;
    private final SurveyTypeRepository surveyTypeRepository;
    private final FeedbackMapper feedbackMapper;

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
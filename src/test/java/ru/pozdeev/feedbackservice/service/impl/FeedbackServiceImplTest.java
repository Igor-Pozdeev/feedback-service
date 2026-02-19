package ru.pozdeev.feedbackservice.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для FeedbackServiceImpl")
class FeedbackServiceImplTest {

    private static final UUID SURVEY_ID = UUID.randomUUID();
    private static final UUID CAMPAIGN_ID = UUID.randomUUID();
    private static final UUID FEEDBACK_ID = UUID.randomUUID();
    private static final String SURVEY_TYPE_CODE = "NPS";
    private static final int VALID_SCORE = 8;
    private static final String COMMENT = "Good service!";

    @Mock
    private SurveyRepository surveyRepository;
    @Mock
    private CustomerFeedbackRepository customerFeedbackRepository;
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private SurveyTypeRepository surveyTypeRepository;
    @Mock
    private FeedbackMapper feedbackMapper;
    @Mock
    private SurveyMapper surveyMapper;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    private SubmitFeedbackRequest request;
    private Survey pendingSurvey;
    private Campaign campaign;
    private SurveyType surveyType;
    private CustomerFeedback customerFeedback;
    private SubmitFeedbackResponse submitFeedbackResponse;

    @BeforeEach
    void setUp() {
        request = new SubmitFeedbackRequest();
        request.setSurveyId(SURVEY_ID);
        request.setScore(VALID_SCORE);
        request.setComment(COMMENT);

        pendingSurvey = new Survey();
        pendingSurvey.setId(SURVEY_ID);
        pendingSurvey.setCampaignId(CAMPAIGN_ID);
        pendingSurvey.setStatus(SurveyStatus.PENDING);

        campaign = new Campaign();
        campaign.setId(CAMPAIGN_ID);
        campaign.setSurveyTypeCode(SURVEY_TYPE_CODE);

        surveyType = new SurveyType();
        surveyType.setCode(SURVEY_TYPE_CODE);
        surveyType.setScaleMin(0);
        surveyType.setScaleMax(10);

        customerFeedback = new CustomerFeedback();
        customerFeedback.setId(FEEDBACK_ID);

        submitFeedbackResponse = new SubmitFeedbackResponse();
        submitFeedbackResponse.setFeedbackId(customerFeedback.getId());
    }

    @Test
    @DisplayName("Успешная отправка отзыва")
    void submitFeedback_whenRequestIsValid_thenSuccess() {
        // Arrange
        when(surveyRepository.findById(SURVEY_ID)).thenReturn(Optional.of(pendingSurvey));
        when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.of(campaign));
        when(surveyTypeRepository.findById(SURVEY_TYPE_CODE)).thenReturn(Optional.of(surveyType));
        when(feedbackMapper.toCustomerFeedback(request, pendingSurvey)).thenReturn(customerFeedback);
        when(customerFeedbackRepository.save(customerFeedback)).thenReturn(customerFeedback);
        when(feedbackMapper.toSubmitFeedbackResponse(customerFeedback)).thenReturn(submitFeedbackResponse);

        // Act
        SubmitFeedbackResponse response = feedbackService.submitFeedback(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFeedbackId()).isEqualTo(customerFeedback.getId());

        verify(surveyRepository).findById(SURVEY_ID);
        verify(campaignRepository).findById(CAMPAIGN_ID);
        verify(surveyTypeRepository).findById(SURVEY_TYPE_CODE);
        verify(customerFeedbackRepository).save(customerFeedback);

        ArgumentCaptor<Survey> surveyCaptor = ArgumentCaptor.forClass(Survey.class);
        verify(surveyRepository).save(surveyCaptor.capture());
        Survey savedSurvey = surveyCaptor.getValue();

        assertThat(savedSurvey.getStatus()).isEqualTo(SurveyStatus.COMPLETED);
        assertThat(savedSurvey.getCompletedAt()).isNotNull().isBeforeOrEqualTo(LocalDateTime.now());

        verify(feedbackMapper).toSubmitFeedbackResponse(customerFeedback);
    }

    @Test
    @DisplayName("Выброс NotFoundException, когда опрос не найден")
    void submitFeedback_whenSurveyNotFound_thenThrowNotFoundException() {
        // Arrange
        when(surveyRepository.findById(SURVEY_ID)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> feedbackService.submitFeedback(request));
        assertThat(exception.getMessage()).isEqualTo("Опрос с id=" + SURVEY_ID + " не найден");

        verifyNoInteractions(campaignRepository, surveyTypeRepository, customerFeedbackRepository, feedbackMapper);
        verify(surveyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Выброс BusinessException, когда опрос не в статусе PENDING")
    void submitFeedback_whenSurveyNotPending_thenThrowBusinessException() {
        // Arrange
        pendingSurvey.setStatus(SurveyStatus.COMPLETED);
        when(surveyRepository.findById(SURVEY_ID)).thenReturn(Optional.of(pendingSurvey));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> feedbackService.submitFeedback(request));
        assertThat(exception.getMessage()).isEqualTo("Опрос " + SURVEY_ID + " не находится в статусе PENDING");

        verifyNoInteractions(campaignRepository, surveyTypeRepository, customerFeedbackRepository, feedbackMapper);
        verify(surveyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Выброс BusinessException, когда кампания не найдена")
    void submitFeedback_whenCampaignNotFound_thenThrowBusinessException() {
        // Arrange
        when(surveyRepository.findById(SURVEY_ID)).thenReturn(Optional.of(pendingSurvey));
        when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> feedbackService.submitFeedback(request));
        assertThat(exception.getMessage()).isEqualTo("Не найдена кампания с id=" + CAMPAIGN_ID);

        verifyNoInteractions(surveyTypeRepository, customerFeedbackRepository, feedbackMapper);
        verify(surveyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Выброс BusinessException, когда тип опроса не найден")
    void submitFeedback_whenSurveyTypeNotFound_thenThrowBusinessException() {
        // Arrange
        when(surveyRepository.findById(SURVEY_ID)).thenReturn(Optional.of(pendingSurvey));
        when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.of(campaign));
        when(surveyTypeRepository.findById(SURVEY_TYPE_CODE)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> feedbackService.submitFeedback(request));
        assertThat(exception.getMessage()).isEqualTo("Не найден тип опроса " + SURVEY_TYPE_CODE);

        verifyNoInteractions(customerFeedbackRepository, feedbackMapper);
        verify(surveyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Выброс BusinessException, когда оценка выходит за допустимый диапазон (ниже)")
    void submitFeedback_whenScoreIsBelowMin_thenThrowBusinessException() {
        // Arrange
        request.setScore(surveyType.getScaleMin() - 1);
        when(surveyRepository.findById(SURVEY_ID)).thenReturn(Optional.of(pendingSurvey));
        when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.of(campaign));
        when(surveyTypeRepository.findById(SURVEY_TYPE_CODE)).thenReturn(Optional.of(surveyType));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> feedbackService.submitFeedback(request));
        assertThat(exception.getMessage()).isEqualTo("Оценка " + request.getScore() + " выходит за пределы допустимого диапазона для типа опроса "
                + SURVEY_TYPE_CODE + " (" + surveyType.getScaleMin() + "-" + surveyType.getScaleMax() + ")");

        verifyNoInteractions(customerFeedbackRepository, feedbackMapper);
        verify(surveyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Выброс BusinessException, когда оценка выходит за допустимый диапазон (выше)")
    void submitFeedback_whenScoreIsAboveMax_thenThrowBusinessException() {
        // Arrange
        request.setScore(surveyType.getScaleMax() + 1);
        when(surveyRepository.findById(SURVEY_ID)).thenReturn(Optional.of(pendingSurvey));
        when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.of(campaign));
        when(surveyTypeRepository.findById(SURVEY_TYPE_CODE)).thenReturn(Optional.of(surveyType));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> feedbackService.submitFeedback(request));
        assertThat(exception.getMessage()).isEqualTo("Оценка " + request.getScore() + " выходит за пределы допустимого диапазона для типа опроса "
                + SURVEY_TYPE_CODE + " (" + surveyType.getScaleMin() + "-" + surveyType.getScaleMax() + ")");

        verifyNoInteractions(customerFeedbackRepository, feedbackMapper);
        verify(surveyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Получение списка доступных опросов, когда они есть")
    void getPendingSurveys_whenSurveysExist_thenReturnsSurveyList() {
        // Arrange
        String guid = "test-guid";

        Survey survey1 = new Survey();
        survey1.setId(UUID.randomUUID());
        survey1.setCampaignId(CAMPAIGN_ID);
        survey1.setSentAt(null);

        List<Survey> surveys = List.of(survey1);

        Campaign campaign1 = new Campaign();
        campaign1.setId(CAMPAIGN_ID);
        campaign1.setSurveyTypeCode(SURVEY_TYPE_CODE);

        SurveyType surveyType1 = new SurveyType();
        surveyType1.setCode(SURVEY_TYPE_CODE);

        when(surveyRepository.findByGuidAndStatusAndScheduledAtBeforeOrderByScheduledAtDesc(
                any(String.class), any(SurveyStatus.class), any(LocalDateTime.class)))
                .thenReturn(surveys);

        when(campaignRepository.findAllById(any())).thenReturn(List.of(campaign1));
        when(surveyTypeRepository.findAllById(any())).thenReturn(List.of(surveyType1));

        SurveyResponse response1 = new SurveyResponse();
        response1.setSurveyId(survey1.getId());

        when(surveyMapper.toSurveyResponse(survey1, campaign1, surveyType1)).thenReturn(response1);

        // Act
        FindSurveysResponse result = feedbackService.getPendingSurveys(guid);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getSurveys()).hasSize(1);
        assertThat(result.getSurveys().get(0).getSurveyId()).isEqualTo(survey1.getId());
        assertThat(survey1.getSentAt()).isNotNull();

        verify(surveyRepository).findByGuidAndStatusAndScheduledAtBeforeOrderByScheduledAtDesc(
                any(String.class), any(SurveyStatus.class), any(LocalDateTime.class));
        verify(campaignRepository).findAllById(any());
        verify(surveyTypeRepository).findAllById(any());
        verify(surveyMapper).toSurveyResponse(survey1, campaign1, surveyType1);
    }
}

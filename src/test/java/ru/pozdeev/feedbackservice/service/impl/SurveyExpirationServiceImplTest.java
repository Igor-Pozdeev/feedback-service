package ru.pozdeev.feedbackservice.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.pozdeev.feedbackservice.common.AbstractTest;
import ru.pozdeev.feedbackservice.property.FeedbackProperties;
import ru.pozdeev.feedbackservice.repository.SurveyRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class SurveyExpirationServiceImplTest extends AbstractTest {

    @Mock
    private SurveyRepository surveyRepository;

    @Mock
    private FeedbackProperties feedbackProperties;

    @InjectMocks
    private SurveyExpirationServiceImpl surveyExpirationService;

    @BeforeEach
    void setUp() {
        when(feedbackProperties.getSentSurveyExpiryDays()).thenReturn(7);
        when(feedbackProperties.getUnsentSurveyExpiryDays()).thenReturn(60);
    }

    @Test
    void expireOldSurveys_shouldExpireSentUnsentAndCampaignEndedSurveys_andReturnTotalCount() {
        // given
        int sentExpired = 5;
        int unsentExpired = 10;
        int campaignEndedExpired = 15;

        when(surveyRepository.expireSentSurveys(any(LocalDateTime.class))).thenReturn(sentExpired);
        when(surveyRepository.expireUnsentSurveys(any(LocalDateTime.class))).thenReturn(unsentExpired);
        when(surveyRepository.expireSurveysWithFinishedCampaigns(any(LocalDateTime.class))).thenReturn(campaignEndedExpired);

        // when
        int totalExpired = surveyExpirationService.expireOldSurveys();

        // then
        assertEquals(sentExpired + unsentExpired + campaignEndedExpired, totalExpired);
        verify(surveyRepository).expireSentSurveys(any(LocalDateTime.class));
        verify(surveyRepository).expireUnsentSurveys(any(LocalDateTime.class));
        verify(surveyRepository).expireSurveysWithFinishedCampaigns(any(LocalDateTime.class));
        verifyNoMoreInteractions(surveyRepository);
    }

    @Test
    void expireOldSurveys_whenNoSurveysToExpire_shouldReturnZero() {
        // given
        when(surveyRepository.expireSentSurveys(any(LocalDateTime.class))).thenReturn(0);
        when(surveyRepository.expireUnsentSurveys(any(LocalDateTime.class))).thenReturn(0);
        when(surveyRepository.expireSurveysWithFinishedCampaigns(any(LocalDateTime.class))).thenReturn(0);

        // when
        int totalExpired = surveyExpirationService.expireOldSurveys();

        // then
        assertEquals(0, totalExpired);
        verify(surveyRepository).expireSentSurveys(any(LocalDateTime.class));
        verify(surveyRepository).expireUnsentSurveys(any(LocalDateTime.class));
        verify(surveyRepository).expireSurveysWithFinishedCampaigns(any(LocalDateTime.class));
        verifyNoMoreInteractions(surveyRepository);
    }
}

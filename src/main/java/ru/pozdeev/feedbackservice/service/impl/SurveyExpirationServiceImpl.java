package ru.pozdeev.feedbackservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pozdeev.feedbackservice.property.FeedbackProperties;
import ru.pozdeev.feedbackservice.repository.SurveyRepository;
import ru.pozdeev.feedbackservice.service.SurveyExpirationService;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyExpirationServiceImpl implements SurveyExpirationService {

    private final SurveyRepository surveyRepository;
    private final FeedbackProperties feedbackProperties;

    @Override
    @Transactional
    public int expireOldSurveys() {
        log.info("Starting to expire old surveys...");

        int expiredBySentDate = surveyRepository.expireSentSurveys(
                LocalDateTime.now().minusDays(feedbackProperties.getSentSurveyExpiryDays())
        );
        log.info("Expired {} surveys based on sent date.", expiredBySentDate);

        int expiredByCreationDate = surveyRepository.expireUnsentSurveys(
                LocalDateTime.now().minusDays(feedbackProperties.getUnsentSurveyExpiryDays())
        );
        log.info("Expired {} surveys based on creation date.", expiredByCreationDate);

        int expiredByCampaignEndDate = surveyRepository.expireSurveysWithFinishedCampaigns(
                LocalDateTime.now()
        );
        log.info("Expired {} surveys based on campaign end date.", expiredByCampaignEndDate);

        int totalExpired = expiredBySentDate + expiredByCreationDate + expiredByCampaignEndDate;
        log.info("Total expired surveys: {}", totalExpired);

        return totalExpired;
    }
}

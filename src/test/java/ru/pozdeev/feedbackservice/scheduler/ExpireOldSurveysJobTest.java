package ru.pozdeev.feedbackservice.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import ru.pozdeev.feedbackservice.common.AbstractTest;
import ru.pozdeev.feedbackservice.model.Survey;
import ru.pozdeev.feedbackservice.model.SurveyStatus;
import ru.pozdeev.feedbackservice.repository.SurveyRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql("/sql/expire_surveys_test_data.sql")
class ExpireOldSurveysJobTest extends AbstractTest {

    @Autowired
    private ExpireOldSurveysJob expireOldSurveysJob;

    @Autowired
    private SurveyRepository surveyRepository;

    @Test
    void whenJobRuns_thenExpiredSurveysAreMarkedAsExpired() {
        // when
        expireOldSurveysJob.run();

        // then
        // Survey to be expired (sent > 7 days ago)
        Optional<Survey> sentExpiredSurvey = surveyRepository.findById(UUID.fromString("c1c1c1c1-1c1c-1c1c-1c1c-1c1c1c1c1c11"));
        assertTrue(sentExpiredSurvey.isPresent());
        assertEquals(SurveyStatus.EXPIRED, sentExpiredSurvey.get().getStatus());

        // Survey to be expired (unsent > 60 days ago)
        Optional<Survey> unsentExpiredSurvey = surveyRepository.findById(UUID.fromString("c1c1c1c1-1c1c-1c1c-1c1c-1c1c1c1c1c12"));
        assertTrue(unsentExpiredSurvey.isPresent());
        assertEquals(SurveyStatus.EXPIRED, unsentExpiredSurvey.get().getStatus());

        // Survey to be expired (campaign ended)
        Optional<Survey> campaignEndedSurvey = surveyRepository.findById(UUID.fromString("c1c1c1c1-1c1c-1c1c-1c1c-1c1c1c1c1c13"));
        assertTrue(campaignEndedSurvey.isPresent());
        assertEquals(SurveyStatus.EXPIRED, campaignEndedSurvey.get().getStatus());

        // Survey NOT to be expired (sent recently)
        Optional<Survey> sentRecentSurvey = surveyRepository.findById(UUID.fromString("c1c1c1c1-1c1c-1c1c-1c1c-1c1c1c1c1c14"));
        assertTrue(sentRecentSurvey.isPresent());
        assertEquals(SurveyStatus.PENDING, sentRecentSurvey.get().getStatus());

        // Survey NOT to be expired (unsent recently)
        Optional<Survey> unsentRecentSurvey = surveyRepository.findById(UUID.fromString("c1c1c1c1-1c1c-1c1c-1c1c-1c1c1c1c1c15"));
        assertTrue(unsentRecentSurvey.isPresent());
        assertEquals(SurveyStatus.PENDING, unsentRecentSurvey.get().getStatus());

        // Survey NOT to be expired (already completed)
        Optional<Survey> completedSurvey = surveyRepository.findById(UUID.fromString("c1c1c1c1-1c1c-1c1c-1c1c-1c1c1c1c1c16"));
        assertTrue(completedSurvey.isPresent());
        assertEquals(SurveyStatus.COMPLETED, completedSurvey.get().getStatus());
    }
}

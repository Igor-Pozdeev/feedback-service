package ru.pozdeev.feedbackservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pozdeev.feedbackservice.service.SurveyExpirationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpireOldSurveysJob {

    private final SurveyExpirationService surveyExpirationService;

    @Scheduled(cron = "0 0 1 * * ?") // Run at 01:00 daily
    @SchedulerLock(name = "expireOldSurveys")
    public void run() {
        log.info("Starting scheduled job to expire old surveys.");
        try {
            int expiredCount = surveyExpirationService.expireOldSurveys();
            log.info("Scheduled job finished. Expired {} surveys.", expiredCount);
        } catch (Exception e) {
            log.error("Error during scheduled job to expire old surveys.", e);
        }
    }
}

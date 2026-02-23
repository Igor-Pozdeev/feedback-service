package ru.pozdeev.feedbackservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pozdeev.feedbackservice.service.MetricsAggregationService;

import java.time.LocalDate;

/**
 * Планировщик для агрегации метрик
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AggregateMetricsJob {

    private final MetricsAggregationService metricsAggregationService;

    /**
     * Запускает агрегацию метрик за вчерашний день.
     * Выполняется ежедневно в 00:05.
     */
    @Scheduled(cron = "0 5 0 * * *")
    @SchedulerLock(name = "aggregateMetrics")
    public void run() {
        log.info("Starting scheduled job to aggregate metrics");
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            metricsAggregationService.aggregateMetricsForDate(yesterday);
            log.info("Scheduled job to aggregate metrics finished successfully for date: {}", yesterday);
        } catch (Exception e) {
            log.error("Error during scheduled job to aggregate metrics", e);
        }
    }
}

package ru.pozdeev.feedbackservice.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pozdeev.feedbackservice.service.MetricsAggregationService;

import java.time.LocalDate;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для AggregateMetricsJob")
class AggregateMetricsJobTest {

    @Mock
    private MetricsAggregationService metricsAggregationService;

    @InjectMocks
    private AggregateMetricsJob aggregateMetricsJob;

    @Test
    @DisplayName("Успешный запуск джоба агрегации метрик за вчерашний день")
    void run_whenSuccess_thenCallServiceWithYesterdayDate() {
        // Arrange
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // Act
        aggregateMetricsJob.run();

        // Assert
        verify(metricsAggregationService).aggregateMetricsForDate(yesterday);
    }

    @Test
    @DisplayName("Обработка исключения при выполнении джоба")
    void run_whenExceptionOccurs_thenHandleException() {
        // Arrange
        LocalDate yesterday = LocalDate.now().minusDays(1);
        doThrow(new RuntimeException("Test exception")).when(metricsAggregationService).aggregateMetricsForDate(yesterday);

        // Act & Assert (exception should not be rethrown)
        aggregateMetricsJob.run();

        verify(metricsAggregationService).aggregateMetricsForDate(yesterday);
    }
}

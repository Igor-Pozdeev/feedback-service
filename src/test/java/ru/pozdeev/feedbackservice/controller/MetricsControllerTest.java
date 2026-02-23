package ru.pozdeev.feedbackservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.pozdeev.feedbackservice.dto.FilterMetricsRequest;
import ru.pozdeev.feedbackservice.dto.FilterMetricsResponse;
import ru.pozdeev.feedbackservice.dto.MetricsSummaryResponse;
import ru.pozdeev.feedbackservice.service.MetricsService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты для MetricsController
 */
@WebMvcTest(MetricsController.class)
class MetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MetricsService metricsService;

    @Test
    @DisplayName("POST /api/v1/metrics/filter: успешное получение метрик")
    void getFilteredMetrics_should_return_200_and_metrics() throws Exception {
        FilterMetricsRequest request = FilterMetricsRequest.builder()
                .campaignId(UUID.randomUUID())
                .type("NPS")
                .dateFrom(LocalDate.of(2024, 1, 1))
                .dateTo(LocalDate.of(2024, 1, 31))
                .build();

        FilterMetricsResponse response = FilterMetricsResponse.builder()
                .metrics(Collections.emptyList())
                .summary(MetricsSummaryResponse.builder()
                        .totalResponses(100)
                        .averageScore(new BigDecimal("42.3"))
                        .periodFrom(LocalDate.of(2024, 1, 1))
                        .periodTo(LocalDate.of(2024, 1, 31))
                        .build())
                .build();

        when(metricsService.getFilteredMetrics(any(FilterMetricsRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/metrics/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.summary.total_responses").value(100))
                .andExpect(jsonPath("$.data.summary.average_score").value(42.3));
    }
}

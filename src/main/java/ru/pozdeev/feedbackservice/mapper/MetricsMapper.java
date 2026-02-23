package ru.pozdeev.feedbackservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pozdeev.feedbackservice.dto.event.DailyMetricsEvent;
import ru.pozdeev.feedbackservice.model.DailyMetrics;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface MetricsMapper {

    @Mapping(target = "eventType", constant = "DAILY_METRICS_AGGREGATED")
    @Mapping(target = "campaignId", source = "dailyMetrics.campaignId")
    @Mapping(target = "campaignName", source = "campaignName")
    @Mapping(target = "date", expression = "java(dailyMetrics.getDate().toLocalDate())")
    @Mapping(target = "type", source = "dailyMetrics.surveyTypeCode")
    @Mapping(target = "totalResponses", source = "dailyMetrics.totalResponses")
    @Mapping(target = "scoreValue", source = "dailyMetrics.scoreValue")
    @Mapping(target = "promoters", source = "dailyMetrics.promoters")
    @Mapping(target = "passives", source = "dailyMetrics.passives")
    @Mapping(target = "detractors", source = "dailyMetrics.detractors")
    @Mapping(target = "timestamp", source = "timestamp")
    DailyMetricsEvent toDailyMetricsEvent(DailyMetrics dailyMetrics, String campaignName, LocalDateTime timestamp);
}

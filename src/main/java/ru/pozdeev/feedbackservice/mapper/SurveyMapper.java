package ru.pozdeev.feedbackservice.mapper;

import java.time.LocalDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pozdeev.feedbackservice.dto.SurveyContextResponse;
import ru.pozdeev.feedbackservice.dto.SurveyResponse;
import ru.pozdeev.feedbackservice.dto.event.CustomerEvent;
import ru.pozdeev.feedbackservice.model.Campaign;
import ru.pozdeev.feedbackservice.model.Survey;
import ru.pozdeev.feedbackservice.model.SurveyStatus;
import ru.pozdeev.feedbackservice.model.SurveyType;

@Mapper(
    componentModel = "spring",
    imports = { SurveyStatus.class, LocalDateTime.class }
)
public interface SurveyMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sentAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "campaignId", source = "campaign.id")
    @Mapping(target = "guid", source = "event.guid")
    @Mapping(target = "contextType", source = "event.contextType")
    @Mapping(target = "contextId", source = "event.contextId")
    @Mapping(target = "status", expression = "java(SurveyStatus.PENDING)")
    @Mapping(
        target = "scheduledAt",
        expression = "java(LocalDateTime.now().plusHours(campaign.getDelayHours()))"
    )
    Survey toSurvey(CustomerEvent event, Campaign campaign);

    @Mapping(target = "surveyId", source = "survey.id")
    @Mapping(target = "campaignId", source = "campaign.id")
    @Mapping(target = "surveyType", source = "surveyType.name")
    @Mapping(target = "question", source = "surveyType.question")
    @Mapping(target = "scaleMin", source = "surveyType.scaleMin")
    @Mapping(target = "scaleMax", source = "surveyType.scaleMax")
    @Mapping(target = "context", source = "survey")
    @Mapping(target = "sentAt", source = "survey.sentAt")
    SurveyResponse toSurveyResponse(Survey survey, Campaign campaign, SurveyType surveyType);

    default SurveyContextResponse toSurveyContextResponse(Survey survey) {
        if (survey == null || survey.getContextType() == null) {
            return null;
        }
        return new SurveyContextResponse(
            survey.getContextType(),
            survey.getContextId(),
            survey.getScheduledAt()
        );
    }
}

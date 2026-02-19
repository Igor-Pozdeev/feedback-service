package ru.pozdeev.feedbackservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pozdeev.feedbackservice.dto.SurveyContextResponse;
import ru.pozdeev.feedbackservice.dto.SurveyResponse;
import ru.pozdeev.feedbackservice.model.Campaign;
import ru.pozdeev.feedbackservice.model.Survey;
import ru.pozdeev.feedbackservice.model.SurveyType;

@Mapper(componentModel = "spring")
public interface SurveyMapper {

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

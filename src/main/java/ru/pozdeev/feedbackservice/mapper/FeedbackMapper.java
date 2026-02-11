package ru.pozdeev.feedbackservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pozdeev.feedbackservice.dto.SubmitFeedbackRequest;
import ru.pozdeev.feedbackservice.dto.SubmitFeedbackResponse;
import ru.pozdeev.feedbackservice.model.CustomerFeedback;
import ru.pozdeev.feedbackservice.model.Survey;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "surveyId", source = "survey.id")
    @Mapping(target = "guid", source = "survey.guid")
    @Mapping(target = "score", source = "request.score")
    @Mapping(target = "comment", source = "request.comment")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createUser", ignore = true)
    @Mapping(target = "lastUpdateTime", ignore = true)
    @Mapping(target = "lastUpdateUser", ignore = true)
    CustomerFeedback toCustomerFeedback(SubmitFeedbackRequest request, Survey survey);

    @Mapping(target = "success", expression = "java(true)")
    @Mapping(target = "message", expression = "java(\"Спасибо за ваш отзыв!\")") // Message will be set in service layer
    @Mapping(source = "id", target = "feedbackId")
    SubmitFeedbackResponse toSubmitFeedbackResponse(CustomerFeedback customerFeedback);
}

package ru.pozdeev.feedbackservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pozdeev.feedbackservice.dto.SubmitFeedbackRequest;
import ru.pozdeev.feedbackservice.dto.SubmitFeedbackResponse;
import ru.pozdeev.feedbackservice.model.CustomerFeedback;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createUser", ignore = true)
    @Mapping(target = "lastUpdateTime", ignore = true)
    @Mapping(target = "lastUpdateUser", ignore = true)
    @Mapping(target = "guid", ignore = true) // GUID will be set from Survey in service layer
    CustomerFeedback toCustomerFeedback(SubmitFeedbackRequest request);

    @Mapping(target = "success", expression = "java(true)")
    @Mapping(target = "message", expression = "java(\"Спасибо за ваш отзыв!\")") // Message will be set in service layer
    @Mapping(source = "id", target = "feedbackId")
    SubmitFeedbackResponse toSubmitFeedbackResponse(CustomerFeedback customerFeedback);
}

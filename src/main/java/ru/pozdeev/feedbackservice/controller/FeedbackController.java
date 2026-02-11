package ru.pozdeev.feedbackservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pozdeev.feedbackservice.dto.SubmitFeedbackRequest;
import ru.pozdeev.feedbackservice.dto.SubmitFeedbackResponse;
import ru.pozdeev.feedbackservice.dto.common.CommonRequest;
import ru.pozdeev.feedbackservice.dto.common.CommonResponse;
import ru.pozdeev.feedbackservice.service.FeedbackService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
@Tag(name = "feedback", description = "API для управления отзывами клиентов")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/submit")
    @Operation(summary = "Сохранить отзыв клиента")
    public CommonResponse<SubmitFeedbackResponse> submitFeedback(@RequestBody @Valid CommonRequest<SubmitFeedbackRequest> request) {
        SubmitFeedbackResponse response = feedbackService.submitFeedback(request.getData());
        return CommonResponse.ok(response);
    }
}
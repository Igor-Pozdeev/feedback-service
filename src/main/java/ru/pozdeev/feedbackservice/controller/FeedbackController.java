package ru.pozdeev.feedbackservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.pozdeev.feedbackservice.dto.FindSurveysResponse;
import ru.pozdeev.feedbackservice.dto.SubmitFeedbackRequest;
import ru.pozdeev.feedbackservice.dto.SubmitFeedbackResponse;
import ru.pozdeev.feedbackservice.dto.common.CommonRequest;
import ru.pozdeev.feedbackservice.dto.common.CommonResponse;
import ru.pozdeev.feedbackservice.service.FeedbackService;

@Validated
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

    @GetMapping("/surveys")
    @Operation(summary = "Получение непройденных опросов", description = "Возвращает список непройденных опросов для клиента.")
    public CommonResponse<FindSurveysResponse> getPendingSurveys(
            @RequestParam @NotBlank(message = "Параметр guid не может быть пустым") String guid) {
        FindSurveysResponse response = feedbackService.getPendingSurveys(guid);
        return CommonResponse.ok(response);
    }
}
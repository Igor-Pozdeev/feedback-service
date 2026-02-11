package ru.pozdeev.feedbackservice.service;

import ru.pozdeev.feedbackservice.dto.SubmitFeedbackRequest;
import ru.pozdeev.feedbackservice.dto.SubmitFeedbackResponse;

public interface FeedbackService {

    /**
     * Обрабатывает отправку отзыва от клиента.
     *
     * @param request Запрос на отправку отзыва.
     * @return Ответ с результатом обработки отзыва.
     */
    SubmitFeedbackResponse submitFeedback(SubmitFeedbackRequest request);
}
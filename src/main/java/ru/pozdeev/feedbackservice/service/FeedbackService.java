package ru.pozdeev.feedbackservice.service;

import ru.pozdeev.feedbackservice.dto.FindSurveysResponse;
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

    /**
     * Возвращает список непройденных опросов для клиента.
     *
     * @param guid Идентификатор клиента.
     * @return Ответ со списком опросов.
     */
    FindSurveysResponse getPendingSurveys(String guid);
}
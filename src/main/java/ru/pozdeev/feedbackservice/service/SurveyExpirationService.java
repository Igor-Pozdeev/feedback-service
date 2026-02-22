package ru.pozdeev.feedbackservice.service;

/**
 * Сервис для управления истечением сроков действия опросов.
 */
public interface SurveyExpirationService {

    /**
     * Обновляет статус устаревших опросов на EXPIRED.
     *
     * @return количество обновленных записей.
     */
    int expireOldSurveys();
}

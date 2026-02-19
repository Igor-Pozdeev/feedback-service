package ru.pozdeev.feedbackservice.service;

import ru.pozdeev.feedbackservice.dto.event.CustomerEvent;

/**
 * Сервис для создания опросов на основе событий от клиентов.
 */
public interface SurveyCreationService {

    /**
     * Обрабатывает событие от клиента и создает опрос, если это необходимо.
     *
     * @param event событие от клиента
     */
    void processCustomerEvent(CustomerEvent event);
}

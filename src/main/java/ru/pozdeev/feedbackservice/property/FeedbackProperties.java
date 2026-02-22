package ru.pozdeev.feedbackservice.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Класс для работы с конфигурационными параметрами, связанными с фидбеком.
 */
@Component
@ConfigurationProperties(prefix = "feedback")
@Getter
@Setter
public class FeedbackProperties {

    /**
     * Количество дней, после которых отправленные опросы считаются истекшими.
     */
    private int sentSurveyExpiryDays;

    /**
     * Количество дней, после которых неотправленные опросы считаются истекшими.
     */
    private int unsentSurveyExpiryDays;
}

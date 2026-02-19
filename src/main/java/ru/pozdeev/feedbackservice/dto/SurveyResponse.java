package ru.pozdeev.feedbackservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для одного опроса в ответе.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResponse {

    /**
     * ID опроса.
     */
    private UUID surveyId;

    /**
     * ID кампании.
     */
    private UUID campaignId;

    /**
     * Тип опроса (например, NPS).
     */
    private String surveyType;

    /**
     * Текст вопроса.
     */
    private String question;

    /**
     * Минимальное значение шкалы.
     */
    private Integer scaleMin;

    /**
     * Максимальное значение шкалы.
     */
    private Integer scaleMax;

    /**
     * Контекст опроса.
     */
    private SurveyContextResponse context;

    /**
     * Время отправки опроса.
     */
    private LocalDateTime sentAt;
}

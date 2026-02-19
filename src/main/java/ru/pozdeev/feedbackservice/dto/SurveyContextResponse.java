package ru.pozdeev.feedbackservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.pozdeev.feedbackservice.model.ContextType;

import java.time.LocalDateTime;

/**
 * DTO для контекста опроса.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyContextResponse {

    /**
     * Тип контекста (например, ORDER, TICKET).
     */
    private ContextType type;

    /**
     * Идентификатор контекста (например, ID заказа).
     */
    @JsonProperty("context_id")
    private String contextId;

    /**
     * Временная метка контекста.
     */
    private LocalDateTime timestamp;
}

package ru.pozdeev.feedbackservice.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.pozdeev.feedbackservice.model.ContextType;
import ru.pozdeev.feedbackservice.model.TriggerType;

import java.time.LocalDateTime;
import java.util.Map;

public record CustomerEvent(
        @NotNull
        TriggerType eventType,
        @NotBlank
        String guid,
        @NotBlank
        String contextId,
        @NotNull
        ContextType contextType,
        @NotNull
        LocalDateTime timestamp,
        Map<String, Object> metadata
) {
}

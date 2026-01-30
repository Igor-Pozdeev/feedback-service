package ru.pozdeev.feedbackservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ на отправку отзыва")
public class SubmitFeedbackResponse {

    @NotNull
    @Schema(description = "Признак успешности операции", example = "true")
    private Boolean success;

    @NotNull
    @Schema(description = "Идентификатор сохраненного отзыва", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID feedbackId;

    @NotNull
    @Schema(description = "Сообщение для пользователя", example = "Спасибо за ваш отзыв!")
    private String message;
}

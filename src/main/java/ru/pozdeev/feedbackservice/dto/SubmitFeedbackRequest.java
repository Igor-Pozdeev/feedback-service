package ru.pozdeev.feedbackservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на отправку отзыва")
public class SubmitFeedbackRequest {

    @NotNull(message = "Идентификатор опроса не может быть пустым")
    @Schema(description = "Идентификатор опроса", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID surveyId;

    @NotNull(message = "Оценка не может быть пустой")
    @Schema(description = "Оценка", example = "9")
    @Min(value = 0, message = "Оценка не может быть меньше 0")
    @Max(value = 10, message = "Оценка не может быть больше 10")
    private Integer score;

    @Schema(description = "Комментарий", example = "Отличный сервис!")
    private String comment;
}

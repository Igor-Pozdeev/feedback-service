package ru.pozdeev.feedbackservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO для списка опросов.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FindSurveysResponse {

    /**
     * Список опросов.
     */
    private List<SurveyResponse> surveys;
}

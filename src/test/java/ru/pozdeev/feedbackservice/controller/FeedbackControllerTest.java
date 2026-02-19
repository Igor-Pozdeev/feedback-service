package ru.pozdeev.feedbackservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import ru.pozdeev.feedbackservice.common.AbstractTest;
import ru.pozdeev.feedbackservice.dto.SubmitFeedbackRequest;
import ru.pozdeev.feedbackservice.dto.common.CommonRequest;

import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("/sql/insert_test_data.sql")
class FeedbackControllerTest extends AbstractTest {

    private static final String BASE_PATH = "/api/v1/feedback";
    private static final UUID PENDING_SURVEY_ID = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
    private static final UUID COMPLETED_SURVEY_ID = UUID.fromString("7c3e1e6d-1b3f-4e4f-8b4b-6b1b1b1b1b1b");
    private static final UUID NOT_FOUND_SURVEY_ID = UUID.randomUUID();

    private static final String GUID_WITH_PENDING_SURVEY_SENT_AT_NULL = "customer-005";
    private static final String GUID_WITH_PENDING_SURVEY_SENT_AT_NOT_NULL = "customer-002";
    private static final String GUID_WITH_COMPLETED_SURVEY = "customer-003";
    private static final String GUID_WITH_NO_SURVEYS = "guid-no-surveys";
    private static final String BLANK_GUID = " ";


    @Test
    @DisplayName("Успешная отправка отзыва")
    void submitFeedback_whenRequestIsValid_shouldReturnSuccess() throws Exception {
        final SubmitFeedbackRequest request = new SubmitFeedbackRequest(PENDING_SURVEY_ID, 10, "All good!");
        CommonRequest<SubmitFeedbackRequest> commonRequest = new CommonRequest<>(request);

        mockMvc.perform(post(BASE_PATH + "/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commonRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true))
                .andExpect(jsonPath("$.data.message").value("Спасибо за ваш отзыв!"))
                .andExpect(jsonPath("$.data.feedbackId").exists());
    }

    @Test
    @DisplayName("Опрос не найден, должно вернуться 404")
    void submitFeedback_whenSurveyNotFound_shouldReturnNotFound() throws Exception {
        final SubmitFeedbackRequest request = new SubmitFeedbackRequest(NOT_FOUND_SURVEY_ID, 5, "comment");
        CommonRequest<SubmitFeedbackRequest> commonRequest = new CommonRequest<>(request);

        mockMvc.perform(post(BASE_PATH + "/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commonRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Опрос не в статусе PENDING, должно вернуться 400")
    void submitFeedback_whenSurveyNotPending_shouldReturnBadRequest() throws Exception {
        final SubmitFeedbackRequest request = new SubmitFeedbackRequest(COMPLETED_SURVEY_ID, 5, "comment");
        CommonRequest<SubmitFeedbackRequest> commonRequest = new CommonRequest<>(request);

        mockMvc.perform(post(BASE_PATH + "/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commonRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Неверный score, должно вернуться 400")
    void submitFeedback_whenScoreIsInvalid_shouldReturnBadRequest() throws Exception {
        final SubmitFeedbackRequest request = new SubmitFeedbackRequest(PENDING_SURVEY_ID, 11, "comment");
        CommonRequest<SubmitFeedbackRequest> commonRequest = new CommonRequest<>(request);

        mockMvc.perform(post(BASE_PATH + "/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commonRequest)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("invalidRequestProvider")
    @DisplayName("Проверка Bean Validation")
    void submitFeedback_whenBeanValidationFails_shouldReturnBadRequest(SubmitFeedbackRequest request, String expectedMessage) throws Exception {
        CommonRequest<SubmitFeedbackRequest> commonRequest = new CommonRequest<>(request);
        mockMvc.perform(post(BASE_PATH + "/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commonRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value(expectedMessage));
    }

    private static Stream<Arguments> invalidRequestProvider() {
        return Stream.of(
                Arguments.of(new SubmitFeedbackRequest(null, 5, "comment"), "Идентификатор опроса не может быть пустым"),
                Arguments.of(new SubmitFeedbackRequest(PENDING_SURVEY_ID, null, "comment"), "Оценка не может быть пустой")
        );
    }

    @Test
    @DisplayName("Получение списка опросов для пользователя, sent_at is null")
    void getPendingSurveys_whenSentAtIsNull_shouldReturnSurveyListAndSetSentAt() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/surveys")
                        .param("guid", GUID_WITH_PENDING_SURVEY_SENT_AT_NULL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.surveys").isArray())
                .andExpect(jsonPath("$.data.surveys.length()").value(1))
                .andExpect(jsonPath("$.data.surveys[0].surveyId",  is("550e8400-e29b-41d4-a716-446655440005")))
                .andExpect(jsonPath("$.data.surveys[0].sentAt", notNullValue()));
    }

    @Test
    @DisplayName("Получение списка опросов для пользователя, sent_at is not null")
    void getPendingSurveys_whenSentAtIsNotNull_shouldReturnSurveyList() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/surveys")
                        .param("guid", GUID_WITH_PENDING_SURVEY_SENT_AT_NOT_NULL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.surveys").isArray())
                .andExpect(jsonPath("$.data.surveys.length()").value(1))
                .andExpect(jsonPath("$.data.surveys[0].surveyId", is("550e8400-e29b-41d4-a716-446655440002")))
                .andExpect(jsonPath("$.data.surveys[0].sentAt").exists());
    }


//    @Test
//    @DisplayName("Получение пустого списка опросов для пользователя, у которого только завершенные опросы")
//    void getPendingSurveys_whenOnlyCompletedSurveysExist_shouldReturnEmptyList() throws Exception {
//        mockMvc.perform(get(BASE_PATH + "/surveys")
//                        .param("guid", GUID_WITH_COMPLETED_SURVEY))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.surveys").isArray())
//                .andExpect(jsonPath("$.data.surveys.length()").value(0));
//    }

//    @Test
//    @DisplayName("Получение пустого списка опросов для пользователя, у которого их нет")
//    void getPendingSurveys_whenNoSurveysExist_shouldReturnEmptyList() throws Exception {
//        mockMvc.perform(get(BASE_PATH + "/surveys")
//                        .param("guid", GUID_WITH_NO_SURVEYS))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.surveys").isArray())
//                .andExpect(jsonPath("$.data.surveys.length()").value(0));
//    }

    @Test
    @DisplayName("Запрос с пустым guid, должно вернуться 400")
    void getPendingSurveys_whenGuidIsBlank_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/surveys")
                        .param("guid", BLANK_GUID))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Запрос без guid, должно вернуться 400")
    void getPendingSurveys_whenNoGuid_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/surveys")
                .param("guid", ""))
                .andExpect(status().isBadRequest());
    }
}

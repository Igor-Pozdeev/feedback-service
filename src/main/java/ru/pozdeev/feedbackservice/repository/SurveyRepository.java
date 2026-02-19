package ru.pozdeev.feedbackservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pozdeev.feedbackservice.model.Survey;
import ru.pozdeev.feedbackservice.model.SurveyStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link Survey}.
 */
@Repository
public interface SurveyRepository extends JpaRepository<Survey, UUID> {

    List<Survey> findByGuidAndStatusAndScheduledAtBeforeOrderByScheduledAtDesc(
            String guid,
            SurveyStatus status,
            LocalDateTime scheduledAt
    );
}

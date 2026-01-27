package ru.pozdeev.feedbackservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pozdeev.feedbackservice.model.Survey;

import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link Survey}.
 */
@Repository
public interface SurveyRepository extends JpaRepository<Survey, UUID> {
}

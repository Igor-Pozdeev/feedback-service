package ru.pozdeev.feedbackservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pozdeev.feedbackservice.model.SurveyType;

public interface SurveyTypeRepository extends JpaRepository<SurveyType, String> {
}

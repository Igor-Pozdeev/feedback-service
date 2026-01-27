package ru.pozdeev.feedbackservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pozdeev.feedbackservice.model.CustomerFeedback;

import java.util.UUID;

public interface CustomerFeedbackRepository extends JpaRepository<CustomerFeedback, UUID> {
}

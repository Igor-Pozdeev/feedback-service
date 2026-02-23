package ru.pozdeev.feedbackservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pozdeev.feedbackservice.model.CustomerFeedback;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CustomerFeedbackRepository extends JpaRepository<CustomerFeedback, UUID> {

    List<CustomerFeedback> findByCreateTimeBetween(LocalDateTime start, LocalDateTime end);
}

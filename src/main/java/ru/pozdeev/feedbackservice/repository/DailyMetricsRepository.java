package ru.pozdeev.feedbackservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.pozdeev.feedbackservice.model.DailyMetrics;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface DailyMetricsRepository extends JpaRepository<DailyMetrics, UUID>, JpaSpecificationExecutor<DailyMetrics> {

    Optional<DailyMetrics> findByCampaignIdAndDate(UUID campaignId, LocalDateTime date);
}

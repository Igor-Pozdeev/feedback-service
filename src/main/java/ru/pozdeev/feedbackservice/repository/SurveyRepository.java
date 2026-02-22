package ru.pozdeev.feedbackservice.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    boolean existsByContextIdAndCampaignId(@NotBlank String s, UUID id);

    @Modifying
    @Query("UPDATE Survey s SET s.status = 'EXPIRED' WHERE s.status = 'PENDING' AND s.sentAt < :expirationTime")
    int expireSentSurveys(@Param("expirationTime") LocalDateTime expirationTime);

    @Modifying
    @Query("UPDATE Survey s SET s.status = 'EXPIRED' WHERE s.status = 'PENDING' AND s.sentAt IS NULL AND s.createTime < :expirationTime")
    int expireUnsentSurveys(@Param("expirationTime") LocalDateTime expirationTime);

    @Modifying
    @Query("UPDATE Survey s SET s.status = 'EXPIRED' WHERE s.status = 'PENDING' AND s.campaign.endDate < :currentTime")
    int expireSurveysWithFinishedCampaigns(@Param("currentTime") LocalDateTime currentTime);
}

package ru.pozdeev.feedbackservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Кампания опроса
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "campaign")
public class Campaign extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Название кампании
     */
    private String name;

    /**
     * Тип опроса
     */
    private String surveyTypeCode;

    /**
     * Тип триггера для запуска опроса
     */
    @Enumerated(EnumType.STRING)
    private TriggerType triggerType;

    /**
     * Задержка в часах перед отправкой опроса (0 - сразу)
     */
    private Integer delayHours;

    /**
     * Активна ли кампания
     */
    private boolean active;

    /**
     * Дата начала кампании
     */
    private LocalDateTime startDate;

    /**
     * Дата окончания кампании
     */
    private LocalDateTime endDate;

    /**
     * Флаг мягкого удаления
     */
    private boolean deleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Campaign campaign = (Campaign) o;
        return id.equals(campaign.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

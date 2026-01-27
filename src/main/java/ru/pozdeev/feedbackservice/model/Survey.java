package ru.pozdeev.feedbackservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


/**
 * Запрос на прохождение опроса клиентом
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "survey")
public class Survey extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Id Кампании, к которой относится опрос
     */
    private UUID campaignId;

    /**
     * Идентификатор клиента
     */
    private String guid;

    /**
     * Тип контекста (заказ, тикет)
     */
    @Enumerated(EnumType.STRING)
    private ContextType contextType;

    /**
     * Идентификатор контекста
     */
    private String contextId;

    /**
     * Статус опроса
     */
    @Enumerated(EnumType.STRING)
    private SurveyStatus status;

    /**
     * Время, когда опрос должен быть отправлен
     */
    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    /**
     * Время фактической отправки опроса
     */
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    /**
     * Время прохождения опроса
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Survey survey = (Survey) o;
        return Objects.equals(id, survey.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

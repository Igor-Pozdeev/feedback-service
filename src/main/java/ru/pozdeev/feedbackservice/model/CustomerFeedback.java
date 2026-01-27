package ru.pozdeev.feedbackservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

/**
 * Ответ клиента на опрос
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_feedback")
public class CustomerFeedback extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Опрос, на который дан ответ
     */
    private UUID surveyId;

    /**
     * Идентификатор клиента
     */
    private String guid;

    /**
     * Оценка, поставленная клиентом
     */
    private Integer score;

    /**
     * Комментарий клиента
     */
    private String comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerFeedback that = (CustomerFeedback) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

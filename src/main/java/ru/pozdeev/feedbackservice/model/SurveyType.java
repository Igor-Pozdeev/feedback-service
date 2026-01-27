package ru.pozdeev.feedbackservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Тип опроса — справочник
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "survey_type")
public class SurveyType extends AuditableEntity {

    /**
     * NPS, CSAT, CES
     */
    @Id
    private String code;

    /**
     * Полное название типа
     */
    private String name;

    /**
     * Текст вопроса для клиента
     */
    private String question;

    /**
     * Минимальное значение шкалы
     */
    private Integer scaleMin;

    /**
     * Максимальное значение шкалы
     */
    private Integer scaleMax;

    /**
     * Описание формулы расчёта (для документации)
     */
    private String formulaDescription;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SurveyType that = (SurveyType) o;
        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

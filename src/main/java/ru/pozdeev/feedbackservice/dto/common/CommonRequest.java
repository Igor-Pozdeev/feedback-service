package ru.pozdeev.feedbackservice.dto.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Getter
@ToString
public class CommonRequest<T> {

    @Valid
    @NotNull
    private final T data;

    @JsonCreator
    public CommonRequest(@JsonProperty("data") T data) {
        this.data = data;
    }
}

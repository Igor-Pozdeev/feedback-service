package ru.pozdeev.feedbackservice.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {

    private final UUID id;
    private final Instant timestamp;
    private final T data;
    private final Error error;

    public CommonResponse(T data, Error error) {
        this.id = UUID.randomUUID();
        this.timestamp = Instant.now();
        this.data = data;
        this.error = error;
    }

    public static <T> CommonResponse<T> ok(T data) {
        return new CommonResponse<>(data, null);
    }

    public static CommonResponse<Void> error(String code) {
        return new CommonResponse<>(null, new Error(code));
    }
}
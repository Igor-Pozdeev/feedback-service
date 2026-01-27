package ru.pozdeev.feedbackservice.model;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class AuditableEntity {

    public static final String DEFAULT_DB_USER = "system";

    private LocalDateTime createTime;
    private String createUser;
    private LocalDateTime lastUpdateTime;
    private String lastUpdateUser;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createTime = now;
        lastUpdateTime = now;
        createUser = DEFAULT_DB_USER;
        lastUpdateUser = DEFAULT_DB_USER;
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdateTime = LocalDateTime.now();
        lastUpdateUser = DEFAULT_DB_USER;
    }
}

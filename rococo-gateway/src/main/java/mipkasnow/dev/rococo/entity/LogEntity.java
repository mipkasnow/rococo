package mipkasnow.dev.rococo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mipkasnow.dev.rococo.log.ApiLogEvent;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@NoArgsConstructor
@Table(name = "logs")
public class LogEntity implements Persistable<UUID> {

    @Id
    @JdbcType(UUIDJdbcType.class)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private Instant occurredAt;

    @Column(nullable = false, length = 64)
    private String ip;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, length = 16)
    private String httpMethod;

    @Column(nullable = false, length = 2048)
    private String path;

    @Column(nullable = false)
    private int httpStatus;

    @Column(nullable = false)
    private boolean success;

    @Column
    private String error;

    @Transient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean isNew = true;

    public LogEntity(ApiLogEvent event) {
        id = event.id();
        occurredAt = event.occurredAt();
        ip = event.ip();
        username = event.username();
        httpMethod = event.httpMethod();
        path = event.path();
        httpStatus = event.httpStatus();
        success = event.success();
        error = event.error();
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostLoad
    @PostPersist
    private void markNotNew() {
        isNew = false;
    }
}

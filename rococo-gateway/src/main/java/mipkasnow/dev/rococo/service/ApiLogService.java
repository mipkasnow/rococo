package mipkasnow.dev.rococo.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import mipkasnow.dev.rococo.entity.LogEntity;
import mipkasnow.dev.rococo.log.ApiLogEvent;
import mipkasnow.dev.rococo.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApiLogService {

    private final LogRepository logRepository;
    private final EntityManager entityManager;

    @Transactional
    public void save(ApiLogEvent event) {
        if (logRepository.existsById(event.id())) {
            return;
        }
        entityManager.persist(entity(event));
    }

    private LogEntity entity(ApiLogEvent event) {
        LogEntity entity = new LogEntity();
        entity.setId(event.id());
        entity.setOccurredAt(event.occurredAt());
        entity.setIp(event.ip());
        entity.setUsername(event.username());
        entity.setHttpMethod(event.httpMethod());
        entity.setPath(event.path());
        entity.setHttpStatus(event.httpStatus());
        entity.setSuccess(event.success());
        entity.setError(event.error());
        return entity;
    }
}

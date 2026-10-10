package mipkasnow.dev.rococo.service;

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

    @Transactional
    public void save(ApiLogEvent event) {
        if (logRepository.existsById(event.id())) {
            return;
        }
        logRepository.save(new LogEntity(event));
    }
}

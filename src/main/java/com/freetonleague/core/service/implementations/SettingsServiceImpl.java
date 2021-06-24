package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.model.SettingsEntity;
import com.freetonleague.core.repository.SettingsRepository;
import com.freetonleague.core.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettingsServiceImpl implements SettingsService {

    private final SettingsRepository settingsRepository;

    private Map<String, String> settingsCache = new HashMap<>();

    @PostConstruct
    @Scheduled(fixedRateString = "${app.common.settings-refresh-rate}")
    private void updateCache() {
        settingsCache = settingsRepository.findAll().stream()
                .collect(Collectors.toMap(SettingsEntity::getKey, SettingsEntity::getValue));
    }

    @Override
    public String getValue(String key) {
        return settingsCache.get(key);
    }

    @Override
    public void forceUpdate() {
        updateCache();
    }

}

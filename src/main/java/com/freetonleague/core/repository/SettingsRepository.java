package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.SettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettingsRepository extends JpaRepository<SettingsEntity, Long> {

    Optional<SettingsEntity> findByKey(String key);

}

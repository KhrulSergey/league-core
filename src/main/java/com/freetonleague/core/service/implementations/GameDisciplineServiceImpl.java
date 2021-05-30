package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.model.GameDiscipline;
import com.freetonleague.core.domain.model.GameDisciplineSettings;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.exception.GameDisciplineSettingsManageException;
import com.freetonleague.core.repository.GameDisciplineRepository;
import com.freetonleague.core.repository.GameDisciplineSettingsRepository;
import com.freetonleague.core.service.GameDisciplineService;
import com.freetonleague.core.util.GameIndicatorConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameDisciplineServiceImpl implements GameDisciplineService {

    private final GameDisciplineSettingsRepository disciplineSettingsRepository;

    private final GameDisciplineRepository disciplineRepository;

    private final Validator validator;
    /**
     * Getting a specific game discipline from DB.
     */
    @Override
    public GameDiscipline getDisciplineById(long id) {
        return disciplineRepository.findById(id).orElse(null);
    }

    /**
     * Getting a list of game disciplines from DB.
     */
    @Override
    public List<GameDiscipline> getAllDisciplines() {
        return disciplineRepository.findAll();
    }

    /**
     * Adding a new game discipline to DB.
     */
    @Override
    public GameDiscipline addDiscipline(GameDiscipline discipline) {
        if (isNull(discipline)) {
            log.error("!> requesting addDiscipline for null discipline. Check evoking clients");
            return null;
        }
        Set<ConstraintViolation<GameDiscipline>> violations = validator.validate(discipline);
        if (!violations.isEmpty()) {
            log.error("!> requesting addDiscipline for discipline with ConstraintViolations. Check evoking clients");
            return null;
        }

        return disciplineRepository.save(discipline);
    }

    /**
     * Edit an existing game discipline in DB.
     */
    @Override
    public GameDiscipline editDiscipline(GameDiscipline discipline) {
        if (isNull(discipline) || isNull(discipline.getId())) {
            log.error("!> requesting editDiscipline for null discipline. Check evoking clients");
            return null;
        }
        Set<ConstraintViolation<GameDiscipline>> violations = validator.validate(discipline);
        if (!violations.isEmpty()) {
            log.error("!> requesting editDiscipline for discipline with ConstraintViolations. Check evoking clients");
            return null;
        }
        return disciplineRepository.save(discipline);
    }

    /**
     * Returns sign of disciplines existence by specified name
     */
    @Override
    public boolean isDisciplineExistByName(String name) {
        if (isBlank(name)) {
            log.error("!> requesting isDisciplineExistByName for Blank name. Check evoking clients");
            return false;
        }
        return disciplineRepository.existsByName(name);
    }

    /**
     * Getting a specific game discipline settings from DB.
     */
    @Override
    public GameDisciplineSettings getDisciplineSettings(long id) {
        return disciplineSettingsRepository.findById(id).orElse(null);
    }

    /**
     * Getting a primary game discipline settings for specific game discipline from DB.
     */
    @Override
    public GameDisciplineSettings getPrimaryDisciplineSettingsByDiscipline(GameDiscipline discipline) {
        if (isNull(discipline)) {
            log.error("!> requesting getPrimaryDisciplineSettingsByDiscipline for null discipline. Check evoking clients");
            return null;
        }
        return this.getPrimaryGameDisciplineSettings(discipline);
    }

    /**
     * Adding a new game discipline settings to DB.
     */
    @Override
    public GameDisciplineSettings addDisciplineSettings(GameDisciplineSettings disciplineSettings) {
        if (isNull(disciplineSettings)) {
            log.error("!> requesting addDisciplineSettings for null disciplineSettings. Check evoking clients");
            return null;
        }
        Set<ConstraintViolation<GameDisciplineSettings>> violations = validator.validate(disciplineSettings);
        if (!violations.isEmpty()) {
            log.error("!> requesting addDisciplineSettings for disciplineSettings with ConstraintViolations. Check evoking clients");
            return null;
        }
        return this.saveGameDisciplineSettings(disciplineSettings);
    }

    /**
     * Edit an existing game discipline settings in DB.
     */
    @Override
    public GameDisciplineSettings editDisciplineSettings(GameDisciplineSettings disciplineSettings) {
        if (isNull(disciplineSettings) || isNull(disciplineSettings.getId())) {
            log.error("!> requesting editDisciplineSettings for null disciplineSettings. Check evoking clients");
            return null;
        }
        Set<ConstraintViolation<GameDisciplineSettings>> violations = validator.validate(disciplineSettings);
        if (!violations.isEmpty()) {
            log.error("!> requesting editDisciplineSettings for disciplineSettings with ConstraintViolations. Check evoking clients");
            return null;
        }
        return this.saveGameDisciplineSettings(disciplineSettings);
    }

    /**
     * Returns sign of discipline settings existence by specified name.
     */
    @Override
    public boolean isDisciplineSettingsExistByName(String name) {
        if (isBlank(name)) {
            log.error("!> requesting isDisciplineSettingsExistByName for Blank name. Check evoking clients");
            return false;
        }
        return disciplineSettingsRepository.existsByName(name);
    }

    /**
     * Save new GameDisciplineSettings with checking for its primary flag
     */
    private GameDisciplineSettings saveGameDisciplineSettings(GameDisciplineSettings disciplineSettings) {
        GameDisciplineSettings primaryDisciplineSettings = getPrimaryGameDisciplineSettings(disciplineSettings.getGameDiscipline());
        if (disciplineSettings.getIsPrimary() && nonNull(primaryDisciplineSettings)) {
            primaryDisciplineSettings.setIsPrimary(false);
            disciplineSettingsRepository.save(primaryDisciplineSettings);
        }
        disciplineSettings.setGameOptimalIndicators(GameIndicatorConverter.convertAndValidate(disciplineSettings.getGameOptimalIndicators()));

        return disciplineSettingsRepository.save(disciplineSettings);
    }

    /**
     * Update GameDisciplineSettings with checking for its primary flag
     */
    private GameDisciplineSettings updateGameDisciplineSettings(GameDisciplineSettings disciplineSettings) {
        GameDisciplineSettings primaryDisciplineSettings = getPrimaryGameDisciplineSettings(disciplineSettings.getGameDiscipline());
        if (!disciplineSettings.getIsPrimary() && primaryDisciplineSettings.equals(disciplineSettings)) {
            log.warn("~ primary flag of discipline settings can't be changed. Settings '{}'", disciplineSettings);
            throw new GameDisciplineSettingsManageException(ExceptionMessages.GAME_DISCIPLINE_SETTINGS_PRIMARY_MODIFICATION_ERROR,
                    "Primary flag of discipline settings can't be changed");
        }
        //check
        disciplineSettings.setGameOptimalIndicators(GameIndicatorConverter.convertAndValidate(disciplineSettings.getGameOptimalIndicators()));

        if (disciplineSettings.getIsPrimary() && nonNull(primaryDisciplineSettings)) {
            primaryDisciplineSettings.setIsPrimary(false);
            disciplineSettingsRepository.save(primaryDisciplineSettings);
        }
        return disciplineSettingsRepository.save(disciplineSettings);
    }

    /**
     * Get primary GameDisciplineSettings for specified discipline
     */
    private GameDisciplineSettings getPrimaryGameDisciplineSettings(GameDiscipline discipline) {
        return disciplineSettingsRepository.findByTruePrimaryAndGameDiscipline(discipline);
    }
}

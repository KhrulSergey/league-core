package com.freetonleague.core.service.tournament.implementations;


import com.freetonleague.core.domain.dto.tournament.GameDisciplineDto;
import com.freetonleague.core.domain.dto.tournament.GameDisciplineSettingsDto;
import com.freetonleague.core.domain.model.tournament.GameDiscipline;
import com.freetonleague.core.domain.model.tournament.GameDisciplineSettings;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.GameDisciplineManageException;
import com.freetonleague.core.exception.GameDisciplineSettingsManageException;
import com.freetonleague.core.exception.UnauthorizedException;
import com.freetonleague.core.exception.ValidationException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.mapper.tournament.GameDisciplineMapper;
import com.freetonleague.core.mapper.tournament.GameDisciplineSettingsMapper;
import com.freetonleague.core.security.permissions.CanManageSystem;
import com.freetonleague.core.service.tournament.GameDisciplineService;
import com.freetonleague.core.service.tournament.RestGameDisciplineFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestGameDisciplineFacadeImpl implements RestGameDisciplineFacade {

    private final GameDisciplineService disciplineService;
    private final GameDisciplineSettingsMapper disciplineSettingsMapper;
    private final GameDisciplineMapper disciplineMapper;
    private final Validator validator;

    /**
     * Getting a specific game discipline from DB.
     */
    @Override
    public GameDisciplineDto getDiscipline(long id, User user) {
        return disciplineMapper.toDto(this.getVerifiedDiscipline(id));
    }

    /**
     * Getting a list of game disciplines from DB.
     */
    @Override
    public List<GameDisciplineDto> getAllDisciplines(User user) {
        return disciplineMapper.toDto(disciplineService.getAllDisciplines());
    }

    /**
     * Adding a new game discipline to DB.
     */
    @CanManageSystem
    @Override
    public GameDisciplineDto addDiscipline(GameDisciplineDto disciplineDto, User user) {
        if (isNull(user)) {
            log.debug("^ user is not authenticate. 'addDiscipline' in RestGameDisciplineFacade request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'addDiscipline' request denied");
        }
        Set<ConstraintViolation<GameDisciplineDto>> violations = validator.validate(disciplineDto);
        if (!violations.isEmpty()) {
            log.debug("^ transmitted GameDisciplineDto: '{}' have constraint violations: '{}'", disciplineDto, violations);
            throw new ConstraintViolationException(violations);
        }
        if (disciplineService.isDisciplineExistByName(disciplineDto.getName())) {
            log.warn("~ parameter 'name' is not unique for addDiscipline");
            throw new ValidationException(ExceptionMessages.GAME_DISCIPLINE_DUPLICATE_BY_NAME_ERROR, "name",
                    "parameter name is not unique for addDiscipline");
        }
        GameDiscipline gameDiscipline = disciplineMapper.fromDto(disciplineDto);
        gameDiscipline.setActive(true);
        gameDiscipline = disciplineService.addDiscipline(gameDiscipline);
        if (isNull(gameDiscipline)) {
            log.error("!> error while creating game discipline from dto '{}' for user '{}'.", disciplineDto, user);
            throw new GameDisciplineManageException(ExceptionMessages.GAME_DISCIPLINE_CREATION_ERROR,
                    "Game discipline was not saved on Portal. Check requested params.");
        }
        return disciplineMapper.toDto(gameDiscipline);
    }

    /**
     * Edit an existing game discipline in DB.
     */
    @CanManageSystem
    @Override
    public GameDisciplineDto editDiscipline(GameDisciplineDto disciplineDto, User user) {
        return null;
    }

    /**
     * Getting a primary game discipline settings for specific game discipline from DB.
     */
    @Override
    public GameDisciplineSettingsDto getPrimaryDisciplineSettingsByDiscipline(long disciplineId, User user) {
        GameDiscipline gameDiscipline = this.getVerifiedDiscipline(disciplineId);

        return disciplineSettingsMapper.toDto(this.getVerifiedPrimaryDisciplineSettingsByDiscipline(gameDiscipline));
    }

    /**
     * Adding a new game discipline settings to DB.
     */
    @CanManageSystem
    @Override
    public GameDisciplineSettingsDto addDisciplineSettings(GameDisciplineSettingsDto disciplineSettingsDto, User user) {
        Set<ConstraintViolation<GameDisciplineSettingsDto>> violations = validator.validate(disciplineSettingsDto);
        if (!violations.isEmpty()) {
            log.debug("^ transmitted GameDisciplineSettingsDto: '{}' have constraint violations: '{}'", disciplineSettingsDto, violations);
            throw new ConstraintViolationException(violations);
        }
        if (disciplineService.isDisciplineSettingsExistByName(disciplineSettingsDto.getName())) {
            log.warn("~ parameter 'name' is not unique for addDisciplineSettings");
            throw new ValidationException(ExceptionMessages.GAME_DISCIPLINE_SETTINGS_DUPLICATE_BY_NAME_ERROR, "name",
                    "parameter name is not unique for addDisciplineSettings");
        }

        GameDiscipline gameDiscipline = this.getVerifiedDiscipline(disciplineSettingsDto.getGameDisciplineId());
        GameDisciplineSettings gameDisciplineSettings = disciplineSettingsMapper.fromDto(disciplineSettingsDto);
        gameDisciplineSettings.setGameDiscipline(gameDiscipline);
        gameDisciplineSettings = disciplineService.addDisciplineSettings(gameDisciplineSettings);
        if (isNull(gameDisciplineSettings)) {
            log.error("!> error while creating game discipline settings from dto '{}' for user '{}'.", disciplineSettingsDto, user);
            throw new GameDisciplineSettingsManageException(ExceptionMessages.GAME_DISCIPLINE_SETTINGS_CREATION_ERROR,
                    "Game discipline settings was not saved on Portal. Check requested params.");
        }
        return disciplineSettingsMapper.toDto(gameDisciplineSettings);
    }

    /**
     * Edit an existing game discipline settings in DB.
     */
    @CanManageSystem
    @Override
    public GameDisciplineSettingsDto editDisciplineSettings(GameDisciplineSettingsDto disciplineSettings, User user) {
        return null;
    }

    /**
     * Getting game discipline info by id and user with privacy check
     */
    @Override
    public GameDiscipline getVerifiedDiscipline(long id) {
        GameDiscipline gameDiscipline = disciplineService.getDisciplineById(id);
        if (isNull(gameDiscipline)) {
            log.debug("^ Game discipline with requested id '{}' was not found. 'getVerifiedDiscipline' in RestGameDisciplineFacade request denied", id);
            throw new GameDisciplineManageException(ExceptionMessages.GAME_DISCIPLINE_NOT_FOUND_ERROR,
                    "Game discipline with requested id " + id + " was not found");
        }
        if (!gameDiscipline.isActive()) {
            log.debug("^ Game discipline with requested id '{}' is not active. 'getVerifiedDiscipline' in RestGameDisciplineFacade request denied", id);
            throw new GameDisciplineManageException(ExceptionMessages.GAME_DISCIPLINE_NOT_ACTIVE_ERROR,
                    "Game discipline with requested id " + id + " is not active");
        }
        return gameDiscipline;
    }

    /**
     * Getting game discipline settings info by id, discipline and user with privacy check
     */
    @Override
    public GameDisciplineSettings getVerifiedDisciplineSettings(long id, GameDiscipline discipline) {
        GameDisciplineSettings gameDisciplineSettings = disciplineService.getDisciplineSettings(id);
        if (isNull(gameDisciplineSettings)) {
            log.debug("^ Game discipline settings with requested id '{}' was not found. " +
                    "'getVerifiedDisciplineSettings' in RestGameDisciplineFacade request denied", id);
            throw new GameDisciplineManageException(ExceptionMessages.GAME_DISCIPLINE_SETTINGS_NOT_FOUND_ERROR,
                    "Game discipline settings with requested id " + id + " was not found");
        }
        if (!gameDisciplineSettings.getGameDiscipline().equals(discipline)) {
            log.debug("^ Game discipline settings with requested id '{}' is not match Game discipline." +
                    " 'getVerifiedDisciplineSettings' in RestGameDisciplineFacade request denied", id);
            throw new GameDisciplineManageException(ExceptionMessages.GAME_DISCIPLINE_SETTINGS_MATCH_DISCIPLINE_ERROR,
                    "Game discipline with requested id " + id + " is not match requested discipline id " + discipline.getId());
        }
        return gameDisciplineSettings;
    }

    /**
     * Getting primary game discipline settings info by game discipline with privacy check
     */
    public GameDisciplineSettings getVerifiedPrimaryDisciplineSettingsByDiscipline(GameDiscipline gameDiscipline) {
        GameDisciplineSettings gameDisciplineSettings = disciplineService.getPrimaryDisciplineSettingsByDiscipline(gameDiscipline);
        if (isNull(gameDisciplineSettings)) {
            log.debug("^ Game discipline settings for requested discipline id '{}' was not found. " +
                    "'getPrimaryDisciplineSettingsByDiscipline' in RestGameDisciplineFacade request denied", gameDiscipline.getId());
            throw new GameDisciplineSettingsManageException(ExceptionMessages.GAME_DISCIPLINE_SETTINGS_NOT_FOUND_ERROR,
                    "Game discipline settings for requested discipline id " + gameDiscipline.getId() + " was not found");
        }
        return gameDisciplineSettings;
    }
}

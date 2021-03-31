package com.freetonleague.core.service;


import com.freetonleague.core.domain.dto.GameDisciplineDto;
import com.freetonleague.core.domain.dto.GameDisciplineSettingsDto;
import com.freetonleague.core.domain.model.User;

import java.util.List;

public interface RestGameDisciplineFacade {

    /**
     * Getting a specific game discipline from DB.
     *
     * @param id   of discipline to search
     * @param user current user from Session
     * @return game discipline with a specific id, null - if the discipline was not found.
     */
    GameDisciplineDto getDiscipline(long id, User user);

    /**
     * Getting a list of game disciplines from DB.
     *
     * @param user current user from Session
     * @return game disciplines, null - if no discipline was found.
     */
    List<GameDisciplineDto> getAllDisciplines(User user);

    /**
     * Adding a new game discipline to DB.
     *
     * @param discipline data to add
     * @param user       current user from Session
     * @return added GameDiscipline
     */
    GameDisciplineDto addDiscipline(GameDisciplineDto discipline, User user);

    /**
     * Edit an existing game discipline in DB.
     *
     * @param discipline data to be modified to the database
     * @param user       current user from Session
     * @return Edited game discipline
     */
    GameDisciplineDto editDiscipline(GameDisciplineDto discipline, User user);


    /**
     * Getting a primary game discipline settings for specific game discipline from DB.
     *
     * @param disciplineId to search primary settings
     * @param user         current user from Session
     * @return game discipline settings for specific game discipline, null - if the settings was not found.
     */
    GameDisciplineSettingsDto getPrimaryDisciplineSettingsByDiscipline(long disciplineId, User user);

    /**
     * Adding a new game discipline settings to DB.
     *
     * @param disciplineSettings data to add
     * @param user               current user from Session
     * @return added GameDisciplineSettings
     */
    GameDisciplineSettingsDto addDisciplineSettings(GameDisciplineSettingsDto disciplineSettings, User user);

    /**
     * Edit an existing game discipline settings in DB.
     *
     * @param disciplineSettings data to be modified to the database
     * @param user               current user from Session
     * @return Edited game discipline settings
     */
    GameDisciplineSettingsDto editDisciplineSettings(GameDisciplineSettingsDto disciplineSettings, User user);
}

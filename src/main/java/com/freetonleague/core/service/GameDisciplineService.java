package com.freetonleague.core.service;


import com.freetonleague.core.domain.model.GameDiscipline;
import com.freetonleague.core.domain.model.GameDisciplineSettings;

import java.util.List;

public interface GameDisciplineService {

    /**
     * Getting a specific game discipline from DB.
     *
     * @param id of discipline to search
     * @return game discipline with a specific id, null - if the discipline was not found.
     */
    GameDiscipline getDisciplineById(long id);

    /**
     * Getting a list of game disciplines from DB.
     *
     * @return game disciplines, null - if no discipline was found.
     */
    List<GameDiscipline> getAllDisciplines();

    /**
     * Adding a new game discipline to DB.
     *
     * @param discipline data to add
     * @return added GameDiscipline
     */
    GameDiscipline addDiscipline(GameDiscipline discipline);

    /**
     * Edit an existing game discipline in DB.
     *
     * @param discipline data to be modified to the database
     * @return Edited game discipline
     */
    GameDiscipline editDiscipline(GameDiscipline discipline);

    /**
     * Returns sign of disciplines existence by specified name.
     *
     * @param name for search discipline entity
     * @return true is disciplines exists, false - if not
     */
    boolean isDisciplineExistByName(String name);

    /**
     * Getting a specific game discipline settings from DB.
     *
     * @param id of discipline settings to search
     * @return game discipline settings with a specific id, null - if the settings was not found.
     */
    GameDisciplineSettings getDisciplineSettings(long id);

    /**
     * Getting a primary game discipline settings for specific game discipline from DB.
     *
     * @param discipline to search primary settings
     * @return game discipline settings for specific game discipline, null - if the settings was not found.
     */
    GameDisciplineSettings getPrimaryDisciplineSettingsByDiscipline(GameDiscipline discipline);

    /**
     * Adding a new game discipline settings to DB.
     *
     * @param disciplineSettings data to add
     * @return added GameDisciplineSettings
     */
    GameDisciplineSettings addDisciplineSettings(GameDisciplineSettings disciplineSettings);

    /**
     * Edit an existing game discipline settings in DB.
     *
     * @param disciplineSettings data to be modified to the database
     * @return Edited game discipline settings
     */
    GameDisciplineSettings editDisciplineSettings(GameDisciplineSettings disciplineSettings);

    /**
     * Returns sign of discipline settings existence by specified name.
     *
     * @param name for search discipline settings entity
     * @return true is discipline settings exists, false - if not
     */
    boolean isDisciplineSettingsExistByName(String name);
}

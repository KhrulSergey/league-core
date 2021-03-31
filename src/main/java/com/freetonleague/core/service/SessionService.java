package com.freetonleague.core.service;


import com.freetonleague.core.domain.model.Session;
import com.freetonleague.core.domain.model.User;

public interface SessionService {


    Session get(String id);

    /** Returns session if it was found in DB or imported from LeagueId-module*/
    Session loadByToken(String token);

    Session getByUser(User user);

    void revoke(Session session);

    void revoke(String token);

    User getCurrentUser();
}

package com.freetonleague.core.service;


import com.freetonleague.core.domain.model.Session;
import com.freetonleague.core.domain.model.User;

import javax.servlet.http.HttpServletRequest;

public interface SessionService {

    /**
     * Save user session by OAuth info. Returns null is the session wasn't created
     */
    Session get(String id);

    Session getByUser(User user);

    void revoke(Session session);

    void revoke(String token);

    User getCurrentUser(HttpServletRequest request);
}

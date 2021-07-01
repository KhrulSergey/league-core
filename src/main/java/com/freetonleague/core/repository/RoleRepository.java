package com.freetonleague.core.repository;

import com.freetonleague.core.domain.enums.UserRoleType;
import com.freetonleague.core.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

/**
 * Access interface for the data of the "Users" entity in the database
 */
public interface RoleRepository extends JpaRepository<Role, Long>,
        JpaSpecificationExecutor<Role> {
    /**
     * Getting user by leagueId
     */
    Role findByName(UserRoleType roleName);

    Set<Role> findAllByNameIn(List<UserRoleType> roleTypeList);

}

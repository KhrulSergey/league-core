package com.freetonleague.core.domain.model;


import com.freetonleague.core.domain.enums.UserRoleType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "roles")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "roles_id_seq", allocationSize = 1)
public class Role extends ExtendedBaseEntity implements GrantedAuthority {

    private static final long serialVersionUID = -2824687278436636781L;

    //Properties
    @Column(name = "name", unique = true)
    @Enumerated(EnumType.STRING)
    private UserRoleType name;

    @Override
    public String getAuthority() {
        return name.toString();
    }
}

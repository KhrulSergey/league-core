package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.UserStatusType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "users_id_seq", allocationSize = 1)
public class User extends BaseEntity  implements UserDetails {

    //Static parameters
    private static final long serialVersionUID = -6645357330555137758L;

    //Properties
    @NotNull
    @Column(name = "league_id", nullable = false)
    private UUID leagueId;

    @NotBlank
    @Column(name = "username", unique = true)
    private String username;

    //TODO сделать конвертер для сохранения и получения пути к файлу Лого (аналогично тому, который будет в league-id)
    @Column(name = "avatar_file_name")
    private String avatarFileName;

    @Column(name = "discord_id")
    private String discordId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStatusType status;

    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}

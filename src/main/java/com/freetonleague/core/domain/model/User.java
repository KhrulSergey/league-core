package com.freetonleague.core.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.freetonleague.core.domain.enums.GameIndicatorType;
import com.freetonleague.core.domain.enums.UserParameterType;
import com.freetonleague.core.domain.enums.UserRoleType;
import com.freetonleague.core.domain.enums.UserStatusType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "users_id_seq", allocationSize = 1)
public class User extends BaseEntity implements UserDetails {

    //Static parameters
    private static final long serialVersionUID = -6645357330555137758L;

    //Properties
    @NotNull
    @Column(name = "league_id", nullable = false)
    private UUID leagueId;

    @Size(max = 40)
    @Column(name = "name")
    private String name;

    @NotBlank
    @Size(max = 50)
    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "avatar_file_name")
    private String avatarHashKey;

    @Column(name = "discord_id")
    private String discordId;

    @Column(name = "email")
    private String email;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStatusType status;

    @Transient
    private UserStatusType prevStatus;

    @Builder.Default
    @Column(name = "is_hidden")
    private boolean isHidden = false;

    @Column(name = "utm_source")
    private String utmSource;

    @Getter
    @Type(type = "jsonb")
    @Column(name = "parameters", columnDefinition = "jsonb")
    private Map<UserParameterType, String> parameters;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    private List<TeamParticipant> userTeamParticipantList;

    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;

    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
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
        return UserStatusType.activeUserStatusList.contains(status);
    }

    public boolean isAdmin() {
        return roles.parallelStream().map(Role::getName).anyMatch(UserRoleType.ADMIN::equals);
    }

    public List<String> getRoleList() {
        return isNotEmpty(this.getRoles()) ?
                this.getRoles().parallelStream().map(Role::getAuthority).collect(Collectors.toList())
                : null;
    }
}

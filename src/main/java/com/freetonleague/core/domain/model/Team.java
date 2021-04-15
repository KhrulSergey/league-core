package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.TeamStateType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

import static java.util.Objects.nonNull;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@Table(schema = "team_management", name = "teams")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "teams_id_seq", schema = "team_management", allocationSize = 1)
public class Team extends BaseEntity {

    //Properties
    @NotBlank
    @Size(max = 25)
    @Column(name = "name", unique = true)
    private String name;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "captain_id", unique = true)
    private TeamParticipant captain;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<TeamParticipant> participantList;

    //TODO сделать конвертер для сохранения и получения пути к файлу Лого (аналогично тому, который будет в league-id)
    @Column(name = "team_logo_file_name")
    private String teamLogoFileName;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TeamStateType status;

    public boolean isCaptain(User user) {
        return captain.getUser().equals(user);
    }

    @Override
    public String toString() {

        String participantListString = (nonNull(participantList) && !participantList.isEmpty()) ?
                ", participantList=" + participantList :
                "";
        String capitanString = nonNull(captain) ? ", captain=" + captain : "";
        return "Team{" +
                "name='" + name + '\'' +
                capitanString +
                participantListString +
                ", teamLogoFileName='" + teamLogoFileName + '\'' +
                ", status=" + status +
                '}';
    }
}

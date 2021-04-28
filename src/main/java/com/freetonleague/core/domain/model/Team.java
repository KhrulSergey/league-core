package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.TeamStateType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

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

    @Setter(AccessLevel.NONE)
    @Column(name = "core_id", nullable = false)
    private UUID coreId;

    @EqualsAndHashCode.Exclude
    @NotNull
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "captain_id", unique = true)
    private TeamParticipant captain;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TeamParticipant> participantList;

    //TODO сделать конвертер для сохранения и получения пути к файлу Лого (аналогично тому, который будет в league-id)
    @Column(name = "team_logo_file_name")
    private String teamLogoFileName;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TeamStateType status;

    @Transient
    private String accountId;

    @PrePersist
    public void prePersist() {
        byte[] uniqueTournamentTimeSlice = this.toString().concat(LocalDateTime.now().toString()).getBytes();
        coreId = UUID.nameUUIDFromBytes(uniqueTournamentTimeSlice);
    }

    public boolean isCaptain(User user) {
        return captain.getUser().equals(user);
    }

    @Override
    public String toString() {

        String participantListString = (nonNull(participantList) && !participantList.isEmpty()) ?
                ", participantList size=" + participantList.size() :
                "";
        String capitanString = nonNull(captain) ? ", captain participant id =" + captain.getId() : "";
        return "Team{" +
                "name='" + name + '\'' +
                capitanString +
                participantListString +
                ", teamLogoFileName='" + teamLogoFileName + '\'' +
                ", status=" + status +
                '}';
    }
}

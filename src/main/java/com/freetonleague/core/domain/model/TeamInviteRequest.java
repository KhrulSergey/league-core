package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.TeamInviteRequestStatusType;
import com.sun.istack.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@EqualsAndHashCode(callSuper = true, exclude = "team")
@Getter
@Setter
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(schema = "team_management", name = "team_invite_requests")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "team_invite_request_id_seq", allocationSize = 1, schema = "team_management")
public class TeamInviteRequest extends BaseEntity implements Serializable {

    //Properties
    @Column(name = "invite_token", unique = true)
    private String inviteToken;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "participant_creator_id")
    private TeamParticipant participantCreator;

    @Column(name = "expiration")
    private LocalDateTime expiration;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TeamInviteRequestStatusType status;


    @Override
    public String toString() {
        String creatorString = nonNull(participantCreator) ?
                String.format("participantCreator=Id:%s, userLeagueId:%s",
                        participantCreator.getId(), participantCreator.getUser().getLeagueId())
                : "";
        String teamString = nonNull(team) ? ", team=id:" + team.getId() : "";
        return "Participant{" +
                ", inviteToken=" + inviteToken +
                creatorString +
                teamString +
                ", status=" + status +
                ", expiration=" + expiration +
                '}';
    }

    public boolean isExpired() {
        return expiration.isBefore(LocalDateTime.now());
    }
}

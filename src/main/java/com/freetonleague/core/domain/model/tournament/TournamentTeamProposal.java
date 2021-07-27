package com.freetonleague.core.domain.model.tournament;

import com.freetonleague.core.domain.dto.finance.AccountTransactionInfoDto;
import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.enums.tournament.TournamentParticipantType;
import com.freetonleague.core.domain.enums.tournament.TournamentTeamParticipantStatusType;
import com.freetonleague.core.domain.enums.tournament.TournamentTeamType;
import com.freetonleague.core.domain.model.BaseEntity;
import com.freetonleague.core.domain.model.Team;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


/**
 * Team proposal to participate in tournament
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(schema = "public", name = "tournament_team_proposal")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "tournament_team_proposal_id_seq", schema = "public", allocationSize = 1)
public class TournamentTeamProposal extends BaseEntity {

    //Properties
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    /**
     * State of team participation in tournament
     */
    @NotNull
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private ParticipationStateType state;

    @Transient
    private ParticipationStateType prevState;

    /**
     * Confirmation of tournament participation from team (check-in)
     */
    @Column(name = "confirmed")
    private Boolean confirmed;

    @Type(type = "jsonb")
    @Column(name = "participate_payment_list", columnDefinition = "jsonb")
    private List<AccountTransactionInfoDto> participatePaymentList;

    /**
     * Type of team that participate in tournament
     */
    @NotNull
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TournamentTeamType type;

    /**
     * Type of participant that apply to tournament
     */
    @NotNull
    @Column(name = "participant_type")
    @Enumerated(EnumType.STRING)
    private TournamentParticipantType participantType;

    /**
     * Team participant list with their role (status) in tournament
     */
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "tournamentTeamProposal", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<TournamentTeamParticipant> tournamentTeamParticipantList;

    @Transient
    private List<TournamentTeamParticipant> mainTournamentTeamParticipantList;

    public List<TournamentTeamParticipant> getMainTournamentTeamParticipantList() {
        if (isNull(mainTournamentTeamParticipantList)) {
            mainTournamentTeamParticipantList = tournamentTeamParticipantList.stream()
                    .filter(p -> p.getStatus() == TournamentTeamParticipantStatusType.MAIN)
                    .collect(Collectors.toList());
        }
        return mainTournamentTeamParticipantList;
    }

    public void setState(ParticipationStateType state) {
        prevState = this.state;
        this.state = state;
    }

    public boolean isStateChanged() {
        return !this.state.equals(this.prevState);
    }

    @Override
    public String toString() {
        String teamId = nonNull(team) ? "team.id=" + team.getId() : "";

        return "TournamentTeamProposal{" +
                teamId +
                ", state=" + state +
                ", prevState=" + prevState +
                ", type=" + type +
                ", participantType=" + participantType +
                '}';
    }
}

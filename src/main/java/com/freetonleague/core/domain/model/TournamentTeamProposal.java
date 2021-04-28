package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.enums.TournamentTeamParticipantStatusType;
import com.freetonleague.core.domain.enums.TournamentTeamStateType;
import com.freetonleague.core.domain.enums.TournamentTeamType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;


/**
 * Team proposal to participate in tournament
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
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
    private TournamentTeamStateType state;

    @Transient
    private TournamentTeamStateType prevState;

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
     * Team participant list with their role (status) in tournament
     */
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "tournamentTeamProposal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TournamentTeamParticipant> tournamentTeamParticipantList;

    @Transient
    private Set<TournamentTeamParticipant> mainTournamentTeamParticipantList;

    public Set<TournamentTeamParticipant> getMainTournamentTeamParticipantList() {
        if (isNull(mainTournamentTeamParticipantList)) {
            mainTournamentTeamParticipantList = tournamentTeamParticipantList.parallelStream()
                    .filter(p -> p.getStatus() == TournamentTeamParticipantStatusType.MAIN)
                    .collect(Collectors.toSet());
        }
        return mainTournamentTeamParticipantList;
    }

    public void setState(TournamentTeamStateType state) {
        prevState = this.state;
        this.state = state;
    }

    public boolean isStateChanged() {
        return !this.state.equals(this.prevState);
    }
}

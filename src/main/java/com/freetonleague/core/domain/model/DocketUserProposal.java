package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.enums.ParticipationStateType;
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

/**
 * User proposals to Universal docket (lists)
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(schema = "public", name = "docket_proposals")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "docket_proposals_id_seq", schema = "public", allocationSize = 1)
public class DocketUserProposal extends BaseEntity {

    //Properties
    @ManyToOne
    @JoinColumn(name = "league_id", referencedColumnName = "league_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docket_id")
    private Docket docket;

    /**
     * Answer of user to docket.textLabel if it was notBlank
     */
    @Column(name = "text_label_answer")
    private String textLabelAnswer;

    /**
     * State of user participation in docket
     */
    @NotNull
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private ParticipationStateType state;

    @Transient
    private ParticipationStateType prevState;

    /**
     * Saved version of  participation fee payment transaction
     */
    @Type(type = "jsonb")
    @Column(name = "participate_payment_list", columnDefinition = "jsonb")
    private List<AccountTransactionInfoDto> participatePaymentList;

    public void setState(ParticipationStateType state) {
        prevState = this.state;
        this.state = state;
    }

    public boolean isStateChanged() {
        return !this.state.equals(this.prevState);
    }
}

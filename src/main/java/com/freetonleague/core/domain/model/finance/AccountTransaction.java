package com.freetonleague.core.domain.model.finance;

import com.freetonleague.core.domain.enums.finance.AccountTransactionStatusType;
import com.freetonleague.core.domain.enums.finance.AccountTransactionTemplateType;
import com.freetonleague.core.domain.enums.finance.AccountTransactionType;
import com.freetonleague.core.domain.model.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.nonNull;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(schema = "league_finance", name = "transactions")
@SequenceGenerator(name = "base_financial_entity_seq", sequenceName = "transactions_id_seq", schema = "league_finance", allocationSize = 1)
public class AccountTransaction extends FinancialBaseEntity implements Serializable {

    private static final long serialVersionUID = -8266518636761230765L;

    //Properties
    @Column(name = "amount")
    private Double amount;

    /**
     * Reference to financial Account as source of fund (inner guid in fin unit)
     */
    @ManyToOne
    @JoinColumn(name = "account_source_guid", referencedColumnName = "guid")
    private Account sourceAccount;

    /**
     * Reference to financial Account as target for fund transfer (inner guid in fin unit)
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "account_target_guid", referencedColumnName = "guid", nullable = false)
    private Account targetAccount;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "parent_transaction_guid", referencedColumnName = "guid")
    private AccountTransaction parentTransaction;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountTransactionStatusType status;

    @Transient
    private AccountTransactionStatusType prevStatus;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private AccountTransactionType transactionType;

    @Column(name = "template_type")
    @Enumerated(EnumType.STRING)
    private AccountTransactionTemplateType transactionTemplateType;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "approved_by_league_id", referencedColumnName = "league_id", nullable = false)
    private User approvedBy;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "aborted_at")
    private LocalDateTime abortedAt;

    private String signature;

    public void setStatus(AccountTransactionStatusType status) {
        prevStatus = this.status;
        this.status = status;
    }

    public boolean isStatusChanged() {
        return !this.status.equals(this.prevStatus);
    }

    @PrePersist
    @PreUpdate
    public void calculateSignature() {
        String sourceAccount = nonNull(getSourceAccount()) ? getSourceAccount().getGUID().toString() : "";
        String targetAccount = nonNull(getTargetAccount()) ? getTargetAccount().getGUID().toString() : "";
        String stringToHash = getGUID().toString() + getAmount().toString() + sourceAccount + targetAccount;
        this.signature = UUID.nameUUIDFromBytes(stringToHash.getBytes(StandardCharsets.UTF_8)).toString();
    }
}

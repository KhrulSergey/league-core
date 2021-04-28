package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.AccountTransactionStatusType;
import com.freetonleague.core.domain.enums.TransactionTemplateType;
import com.freetonleague.core.domain.enums.TransactionType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Data
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

    @Column(name = "name")
    private String name;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountTransactionStatusType status = AccountTransactionStatusType.FINISHED;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "template_type")
    @Enumerated(EnumType.STRING)
    private TransactionTemplateType transactionTemplateType;
}

package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.AccountStatusType;
import com.freetonleague.core.domain.enums.AccountType;
import com.freetonleague.core.domain.enums.BankProviderType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Data
@Entity
@Table(schema = "league_finance", name = "accounts")
@SequenceGenerator(name = "base_financial_entity_seq", sequenceName = "accounts_id_seq", schema = "league_finance", allocationSize = 1)
public class Account extends FinancialBaseEntity implements Serializable {

    private static final long serialVersionUID = 7314256271219020959L;

    //Properties
    @ManyToOne
    @JoinColumn(name = "holder_guid", referencedColumnName = "guid")
    private AccountHolder holder;

    /**
     * For external fin account with unknown holder
     */
    @Column(name = "is_not_tracking")
    private Boolean isNotTracking;

    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "amount")
    private Double amount;

    /**
     * Finance type of account
     */
    @NotNull
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    /**
     * Status of account
     */
    @NotNull
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatusType status = AccountStatusType.ACTIVE;

    /**
     * External account number (id, address)
     */
    @Column(name = "external_address")
    private String externalAddress;

    @Column(name = "external_bank_type")
    @Enumerated(EnumType.STRING)
    private BankProviderType externalBankType;

    @Column(name = "external_bank_last_update_at")
    private LocalDateTime externalBankLastUpdate;
}

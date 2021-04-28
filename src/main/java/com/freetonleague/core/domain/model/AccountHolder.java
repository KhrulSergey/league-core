package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.AccountHolderType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Data
@Entity
@Table(schema = "league_finance", name = "account_holders")
@SequenceGenerator(name = "base_financial_entity_seq", sequenceName = "account_holders_id_seq", schema = "league_finance", allocationSize = 1)
public class AccountHolder extends FinancialBaseEntity implements Serializable {

    private static final long serialVersionUID = 7256346161615025332L;

    //Properties
    /**
     * Ref to leagueId of User or coreId of Tournament
     */
    @NotNull
    @Column(name = "holder_external_guid", nullable = false)
    private UUID holderExternalGUID;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "holder_type", nullable = false)
    private AccountHolderType holderType;

    @Column(name = "holder_name")
    private String holderName;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "holder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> account;


    public Account getAccount() {
        return account.get(0);
    }

    public void setAccount(Account account) {
        this.account = Collections.singletonList(account);
    }
}

package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.AccountStatusType;
import com.freetonleague.core.domain.enums.AccountType;
import com.freetonleague.core.domain.enums.BankProviderType;
import lombok.*;
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
@ToString(callSuper = true)
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
    @Builder.Default
    @Column(name = "is_not_tracking")
    private Boolean isNotTracking = false;

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

    private String signature;
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

    public Boolean getIsNotTracking() {
        return nonNull(isNotTracking) ? isNotTracking : false;
    }

    @PrePersist
    @PreUpdate
    public void calculateSignature() {
        String stringToHash = String.valueOf(getGUID()) + getAmount();
        this.signature = UUID.nameUUIDFromBytes(stringToHash.getBytes(StandardCharsets.UTF_8)).toString();
    }
}

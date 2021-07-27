package com.freetonleague.core.domain.model.finance;

import com.freetonleague.core.domain.enums.finance.Currency;
import com.freetonleague.core.domain.enums.finance.CurrencyMarketProviderType;
import com.freetonleague.core.domain.enums.finance.CurrencyPairType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Entity
@Table(schema = "league_finance", name = "exchange_ratios")
@SequenceGenerator(name = "base_financial_entity_seq", sequenceName = "exchange_ratio_id_seq", schema = "league_finance", allocationSize = 1)
/**
 * Model for represent exchange currency ratio. Note: exchangeRatio * amount_to_sell = amount_to_buy
 */
public class ExchangeRatio extends FinancialBaseEntity {

    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    private CurrencyMarketProviderType provider;

    @Column(name = "currency_pair")
    @Enumerated(EnumType.STRING)
    private CurrencyPairType currencyPairType;

    @Column(name = "currency_to_buy")
    @Enumerated(EnumType.STRING)
    private Currency currencyToBuy;

    @Column(name = "currency_to_sell")
    @Enumerated(EnumType.STRING)
    private Currency currencyToSell;

    @Column(name = "ratio")
    private Double ratio;

    @Type(type = "jsonb")
    @Column(name = "exchange_currency_rate_raw", columnDefinition = "jsonb")
    private Object exchangeCurrencyRateRawData;

    /**
     * Field of connection from parent to child exchange ratios (currency exchange sequence)
     */
    @EqualsAndHashCode.Exclude
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(schema = "league_finance", name = "exchange_ratio_parents",
            joinColumns = @JoinColumn(name = "current_ratio_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "parent_ratio_id", referencedColumnName = "id"))
    private List<ExchangeRatio> parentExchangeRatioList;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;
}

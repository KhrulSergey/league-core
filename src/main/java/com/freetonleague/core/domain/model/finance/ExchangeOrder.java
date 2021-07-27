package com.freetonleague.core.domain.model.finance;

import com.freetonleague.core.domain.dto.finance.PaymentInvoiceDto;
import com.freetonleague.core.domain.enums.finance.Currency;
import com.freetonleague.core.domain.enums.finance.ExchangeOrderStatus;
import com.freetonleague.core.domain.enums.finance.PaymentGatewayProviderType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, of = {"currencyToBuy", "amountToBuy", "currencyToSell", "amountToSell", "exchangeRatio", "status"})
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(schema = "league_finance", name = "exchange_orders")
@SequenceGenerator(name = "base_financial_entity_seq", sequenceName = "exchange_order_id_seq", schema = "league_finance", allocationSize = 1)
public class ExchangeOrder extends FinancialBaseEntity {

    @NotNull
    @Column(name = "currency_to_buy")
    private Currency currencyToBuy;

    /**
     * Note: exchangeRatio * amount_to_sell = amount_to_buy
     */
    @NotNull
    @Column(name = "amount_to_buy")
    private Double amountToBuy;

    @NotNull
    @Column(name = "currency_to_sell")
    private Currency currencyToSell;

    /**
     * Note: amount_to_sell = amount_to_buy / exchangeRatio
     */
    @NotNull
    @Column(name = "amount_to_sell")
    private Double amountToSell;

    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "exchange_ratio_guid", referencedColumnName = "guid")
    private ExchangeRatio exchangeRatio;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ExchangeOrderStatus status;

    @Column(name = "payment_url")
    private String paymentUrl;

    @Column(name = "payment_gateway")
    private PaymentGatewayProviderType paymentGateway;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "client_account_guid", referencedColumnName = "guid", nullable = false)
    private Account clientAccount;

    @ManyToOne
    @JoinColumn(name = "payment_transaction_guid", referencedColumnName = "guid")
    private AccountTransaction paymentTransaction;

    @Type(type = "jsonb")
    @Column(name = "payment_invoice_raw", columnDefinition = "jsonb")
    private PaymentInvoiceDto paymentInvoiceRaw;

    @JoinColumn(name = "expired_at")
    private LocalDateTime expiredAt;

    @JoinColumn(name = "finished_at")
    private LocalDateTime finishedAt;
}

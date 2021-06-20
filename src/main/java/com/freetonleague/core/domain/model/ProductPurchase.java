package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.dto.ProductPropertyDto;
import com.freetonleague.core.domain.enums.PurchaseStateType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Product purchases of users
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true, of = {"user", "product", "purchaseQuantity", "state"})
@Getter
@Setter
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(schema = "public", name = "product_purchases")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "product_purchases_id_seq", schema = "public", allocationSize = 1)
public class ProductPurchase extends ExtendedBaseEntity {

    //Properties
    @ManyToOne
    @JoinColumn(name = "league_id", referencedColumnName = "league_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Setter(AccessLevel.NONE)
    @Column(name = "core_id", nullable = false, updatable = false)
    private UUID coreId;

    /**
     * Selected parameters (text labels) for product
     */
    @Type(type = "jsonb")
    @Column(name = "selected_product_parameters", columnDefinition = "jsonb")
    private List<ProductPropertyDto> selectedProductParameters;

    @Column(name = "purchase_quantity")
    private Double purchaseQuantity;

    @NotNull
    @Column(name = "purchase_total_amount")
    private Double purchaseTotalAmount;

    /**
     * State of purchase
     */
    @NotNull
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private PurchaseStateType state;

    @Transient
    private PurchaseStateType prevState;

    @Column(name = "buyer_comment")
    private String buyerComment;

    @Column(name = "managerComment")
    private String managerComment;

    /**
     * Saved version of purchase payment transaction
     */
    @Type(type = "jsonb")
    @Column(name = "purchase_payment_list", columnDefinition = "jsonb")
    private List<AccountTransactionInfoDto> purchasePaymentList;

    public void setState(PurchaseStateType state) {
        prevState = this.state;
        this.state = state;
    }

    @PrePersist
    public void prePersist() {
        byte[] uniqueTournamentTimeSlice = this.toString().concat(LocalDateTime.now().toString()).getBytes();
        coreId = UUID.nameUUIDFromBytes(uniqueTournamentTimeSlice);
    }

    public boolean isStateChanged() {
        return !this.state.equals(this.prevState);
    }
}

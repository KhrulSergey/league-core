package com.freetonleague.core.domain.model.product;

import com.freetonleague.core.domain.dto.product.ProductPropertyDto;
import com.freetonleague.core.domain.enums.AccessType;
import com.freetonleague.core.domain.enums.ProductStatusType;
import com.freetonleague.core.domain.model.ExtendedBaseEntity;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;

/**
 * Product
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true, of = {"name", "status"})
@Getter
@Setter
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(schema = "public", name = "products")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "products_id_seq", schema = "public", allocationSize = 1)
public class Product extends ExtendedBaseEntity {

    //Properties
    @NotBlank
    @Column(name = "name")
    private String name;

    @Setter(AccessLevel.NONE)
    @Column(name = "core_id", nullable = false, updatable = false)
    private UUID coreId;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    /**
     * Detailed parameters (text labels) for product
     */
    @Type(type = "jsonb")
    @Column(name = "product_parameters", columnDefinition = "jsonb")
    private List<ProductPropertyDto> productParameters;

    @Builder.Default
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProductStatusType status = ProductStatusType.ACTIVE;

    @Transient
    private ProductStatusType prevStatus;

    @Builder.Default
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "access_type")
    private AccessType accessType = AccessType.PAID_ACCESS;

    @Column(name = "cost")
    private Double cost;

    @Column(name = "quantity_in_stock")
    private Double quantityInStock;

    @Column(name = "possible_quantity_step")
    private Double possibleQuantityStep;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "product", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    private List<ProductPurchase> userProductPurchase;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    public void setStatus(ProductStatusType status) {
        prevStatus = this.status;
        this.status = status;
    }

    @PrePersist
    public void prePersist() {
        byte[] uniqueTournamentTimeSlice = this.toString().concat(LocalDateTime.now().toString()).getBytes();
        coreId = UUID.nameUUIDFromBytes(uniqueTournamentTimeSlice);
    }

    public boolean isStatusChanged() {
        return !this.status.equals(this.prevStatus);
    }

    public boolean hasQuantityLimit() {
        return nonNull(quantityInStock);
    }
}

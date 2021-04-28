package com.freetonleague.core.domain.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * Base model for all financial project entities
 */
@EqualsAndHashCode
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Audited(targetAuditMode = NOT_AUDITED)
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class FinancialBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "base_financial_entity_seq")
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Setter(AccessLevel.PRIVATE)
    @Column(name = "guid", nullable = false)
    private UUID GUID;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @CreatedBy
    @JoinColumn(name = "created_by_league_id", referencedColumnName = "league_id", nullable = false, updatable = false)
    private User createdBy;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @LastModifiedBy
    @JoinColumn(name = "modified_by_league_id", referencedColumnName = "league_id", nullable = false)
    private User modifiedBy;

    @PrePersist
    public void prePersist() {
        this.generateGUID();
    }

    public void generateGUID() {
        if (isNull(GUID)) {
            byte[] uniqueTournamentTimeSlice = this.toString().concat(LocalDateTime.now().toString()).getBytes();
            GUID = UUID.nameUUIDFromBytes(uniqueTournamentTimeSlice);
        }
    }
}

package com.freetonleague.core.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * Extended base model for highly responsible (in business meaning) entities
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@Audited(targetAuditMode = NOT_AUDITED)
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class ExtendedBaseEntity extends BaseEntity {

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


}


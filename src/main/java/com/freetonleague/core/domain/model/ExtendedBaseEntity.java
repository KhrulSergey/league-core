package com.freetonleague.core.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
/**
 * Extended base model for highly responsible (in business meaning) entities
 * */
public class ExtendedBaseEntity extends BaseEntity {

    @ManyToOne
    @CreatedBy
    @JoinColumn(name = "created_by_league_id", referencedColumnName = "league_id", updatable = false)
    private User createdBy;

    @ManyToOne
    @LastModifiedBy
    @JoinColumn(name = "modified_by_league_id", referencedColumnName = "league_id")
    private User modifiedBy;

}


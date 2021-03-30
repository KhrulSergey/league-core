package com.freetonleague.core.domain.model;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@Data
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
/**
 * Extended base model for highly responsible (in business meaning) entities
 * */
public class ExtendedBaseEntity extends BaseEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "created_by_league_id", referencedColumnName = "league_id", nullable = false)
    private User created_by;


    @ManyToOne
    @JoinColumn(name = "modified_by_league_id", referencedColumnName = "league_id", nullable = false)
    private User modified_by;

}

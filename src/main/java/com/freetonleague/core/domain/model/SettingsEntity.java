package com.freetonleague.core.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(schema = "public", name = "settings")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "settings_id_seq", schema = "public", allocationSize = 1)
public class SettingsEntity extends BaseEntity {

    private String key;

    private String value;

    private String description;

}

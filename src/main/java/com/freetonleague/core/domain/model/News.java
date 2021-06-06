package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.NewsStatusType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Universal docket (lists) for different purpose
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(schema = "public", name = "news")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "news_id_seq", schema = "public", allocationSize = 1)
public class News extends ExtendedBaseEntity {

    //Properties
    @NotBlank
    @Column(name = "title")
    private String title;

    @Column(name = "theme")
    private String theme;

    @Column(name = "image_url")
    private String imageUrl;

    @NotBlank
    @Column(name = "description")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private NewsStatusType status = NewsStatusType.ACTIVE;

    @Type(type = "jsonb")
    @Column(name = "tags", columnDefinition = "jsonb")
    private List<String> tags;

    @Transient
    private NewsStatusType prevStatus;

    public void setStatus(NewsStatusType status) {
        prevStatus = this.status;
        this.status = status;
    }

    public boolean isStatusChanged() {
        return !this.status.equals(this.prevStatus);
    }
}

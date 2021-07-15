package com.freetonleague.core.domain.model.docket;

import com.freetonleague.core.domain.enums.AccessType;
import com.freetonleague.core.domain.enums.DocketStatusType;
import com.freetonleague.core.domain.enums.DocketSystemType;
import com.freetonleague.core.domain.model.ExtendedBaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Universal docket (lists) for different purpose
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true, of = {"name", "status"})
@Getter
@Setter
@Entity
@Table(schema = "public", name = "dockets")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "dockets_id_seq", schema = "public", allocationSize = 1)
public class Docket extends ExtendedBaseEntity {

    //Properties
    @NotBlank
    @Column(name = "name")
    private String name;

    @Setter(AccessLevel.NONE)
    @Column(name = "core_id", nullable = false, updatable = false)
    private UUID coreId;

    @Column(name = "description")
    private String description;

    /**
     * Text label (mark, question) for docket tournament status
     */
    @Size(max = 900)
    @Column(name = "text_label")
    private String textLabel;

    @Builder.Default
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DocketStatusType status = DocketStatusType.CREATED;

    @Transient
    private DocketStatusType prevStatus;

    @Builder.Default
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "access_type")
    private AccessType accessType = AccessType.FREE_ACCESS;

    @Builder.Default
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "system_type")
    private DocketSystemType systemType = DocketSystemType.DEFAULT;

    @Column(name = "image_url")
    private String imageUrl;

    /**
     * Default or minimum participation fee
     */
    @Column(name = "participation_fee")
    private Double participationFee;

    /**
     * Maximum participation fee
     */
    @Column(name = "max_participation_fee")
    private Double maxParticipationFee;

    @Column(name = "max_proposal_count")
    private Integer maxProposalCount;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "docket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DocketUserProposal> userProposalList;

    @Column(name = "sign_up_start_at")
    private LocalDateTime signUpStartDate;

    @Column(name = "sign_up_ends_at")
    private LocalDateTime signUpEndDate;

    @Column(name = "start_planned_at")
    private LocalDateTime startPlannedDate;

    @Column(name = "finished_at")
    private LocalDateTime finishedDate;

    @ManyToOne
    private DocketPromoEntity promo;

    public void setStatus(DocketStatusType status) {
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

    public boolean hasTextLabel() {
        return !isBlank(textLabel);
    }

    public boolean hasProposalCountLimit() {
        return nonNull(maxProposalCount);
    }
}

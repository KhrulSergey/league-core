package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.AccessType;
import com.freetonleague.core.domain.enums.DocketStatusType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Universal docket (lists) for different purpose
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
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
    @NotBlank
    @Column(name = "status")
    private DocketStatusType status = DocketStatusType.CREATED;

    @Transient
    private DocketStatusType prevStatus;

    @Builder.Default
    @NotBlank
    @Column(name = "access_type")
    private AccessType accessType = AccessType.FREE_ACCESS;

    @Builder.Default
    @Column(name = "participation_fee")
    private Double participationFee = 0.0;

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
}

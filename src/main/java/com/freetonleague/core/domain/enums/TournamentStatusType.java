package com.freetonleague.core.domain.enums;

import java.util.List;

public enum TournamentStatusType {
    CREATED(1), // just created and started advertising
    SIGN_UP(2), // ready to collect proposals
    ADJUSTMENT(3), // approve all proposals
    STARTED(4), // started and mathes began
    PAUSE(5), // pause for further instructions
    FINISHED(6), // successfully finished
    DECLINED(0), //  canceled before started
    DELETED(-1) // not seen to anybody, just save in DB
    ;

    private final int sequencePosition;

    TournamentStatusType(int sequencePosition) {
        this.sequencePosition = sequencePosition;
    }

    public int getSequencePosition() {
        return sequencePosition;
    }

    public boolean isBefore(TournamentStatusType compare) {
        return this.sequencePosition < compare.getSequencePosition();
    }

    public boolean isFinished() {
        return this == FINISHED;
    }

    public boolean isDeleted() {
        return this == DELETED;
    }

    public static List<TournamentStatusType> getFinishedStatusList() {
        return List.of(
                TournamentStatusType.FINISHED,
                TournamentStatusType.DECLINED,
                TournamentStatusType.DELETED
        );
    }

    public boolean isCreated() {
        return this == CREATED;
    }
}
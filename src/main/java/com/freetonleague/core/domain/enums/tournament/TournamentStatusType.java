package com.freetonleague.core.domain.enums.tournament;

import java.util.List;

public enum TournamentStatusType {
    CREATED(1), // just created and started advertising
    SIGN_UP(2), // ready to collect proposals
    ADJUSTMENT(3), // time to approve/reject all proposals
    STARTED(4), // started and matches began
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

    public boolean isCreated() {
        return this == CREATED;
    }

    public boolean isDeclined() {
        return this == DECLINED;
    }

    /**
     * Returns "active" statuses for tournaments
     */
    public static final List<TournamentStatusType> activeStatusList = List.of(
            TournamentStatusType.CREATED,
            TournamentStatusType.SIGN_UP,
            TournamentStatusType.ADJUSTMENT,
            TournamentStatusType.STARTED,
            TournamentStatusType.PAUSE
    );

    /**
     * Returns "started" statuses for tournaments
     */
    public static final List<TournamentStatusType> startedStatusList = List.of(
            TournamentStatusType.STARTED,
            TournamentStatusType.PAUSE,
            TournamentStatusType.FINISHED
    );
    /**
     * Returns "finished" statuses for tournaments
     */
    public static final List<TournamentStatusType> finishedStatusList = List.of(
            TournamentStatusType.FINISHED,
            TournamentStatusType.DECLINED,
            TournamentStatusType.DELETED
    );
    /**
     * Returns "canceled" statuses for tournaments
     */
    public static final List<TournamentStatusType> canceledStatusList = List.of(
            TournamentStatusType.DECLINED,
            TournamentStatusType.DELETED
    );

    /**
     * Returns posible status for "check-in" statuses for tournaments
     */
    public static final List<TournamentStatusType> checkInStatusList = List.of(
            TournamentStatusType.SIGN_UP,
            TournamentStatusType.ADJUSTMENT
    );
}

package com.freetonleague.core.domain.enums;

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
}

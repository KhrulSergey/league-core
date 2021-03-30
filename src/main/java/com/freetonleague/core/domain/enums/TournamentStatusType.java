package com.freetonleague.core.domain.enums;

public enum TournamentStatusType {
    CREATED, // just created and started advertising
    SIGN_UP, // ready to collect proposals
    ADJUSTMENT, // approve all proposals
    STARTED, // started and mathes began
    PAUSE, // pause for further instructions
    FINISHED, // successfully finished
    DECLINED, //  canceled before started
    DELETED // not seen to anybody, just save in DB
}

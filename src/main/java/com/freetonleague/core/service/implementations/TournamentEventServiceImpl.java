package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.dto.EventDto;
import com.freetonleague.core.domain.enums.EventOperationType;
import com.freetonleague.core.domain.enums.EventProducerModelType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.service.EventService;
import com.freetonleague.core.service.TournamentEventService;
import com.freetonleague.core.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class TournamentEventServiceImpl implements TournamentEventService {

    private final Set<Long> cachedTournamentId = Collections.synchronizedSet(new HashSet<>());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final EventService eventService;
    private final TournamentService tournamentService;

    @Override
    public EventDto add(EventDto event) {
        log.info("! handle add EventDto");
        return null;
    }

    //every 10 minutes
    @Scheduled(fixedRateString = "600000")
    void monitor() {
        log.debug("^ Run TournamentEventService monitor");

        final Map<Long, Tournament> idToTournament = getIdToTournamentMap();
        final Set<Long> keys = new HashSet<>(idToTournament.keySet());

        if (idToTournament.isEmpty()) {
            log.debug("^ Active tournaments were not found. TournamentEventService monitor waits.");
            return;
        }

        if (!cachedTournamentId.isEmpty() && cachedTournamentId.containsAll(keys)) {
            log.debug("^ Tournament events cache was cleaned");
            cachedTournamentId.clear();
        } else {
            keys.removeAll(cachedTournamentId);
        }

        for (Long selectedKey : keys) {
            Tournament tournament = idToTournament.get(selectedKey);
            tryMakeStatusUpdateOperations(tournament);
        }
    }

    private Map<Long, Tournament> getIdToTournamentMap() {
        return Collections.unmodifiableMap(
                tournamentService.getAllActiveTournament()
                        .stream()
                        .collect(Collectors.toMap(Tournament::getId, tournament -> tournament)));

    }

    private void tryMakeStatusUpdateOperations(Tournament tournament) {
        log.debug("^ try to determinate events for tournament: {}", tournament.getId());
        final TournamentStatusType tournamentStatus = tournament.getStatus();

        if (tournamentStatus.isBefore(TournamentStatusType.SIGN_UP)
                && tournament.getSignUpStartDate().isBefore(LocalDateTime.now())) {
            this.handleStatusChange(tournament, TournamentStatusType.SIGN_UP);
        } else if (tournamentStatus.isBefore(TournamentStatusType.ADJUSTMENT)
                && tournament.getSignUpEndDate().isBefore(LocalDateTime.now())) {
            this.handleStatusChange(tournament, TournamentStatusType.ADJUSTMENT);
        } else if (tournamentStatus.isBefore(TournamentStatusType.STARTED)
                && tournament.getStartPlannedDate().isBefore(LocalDateTime.now())) {
            this.handleStatusChange(tournament, TournamentStatusType.STARTED);
        }
        log.debug("^ tournament {} with status {} were checked, and added to cache", tournament.getName(), tournament.getId());
        cachedTournamentId.add(tournament.getId());
    }

    private void handleStatusChange(Tournament tournament, TournamentStatusType newTournamentStatus) {

        Map<String, Object> updateFields = Map.of(
                "status", newTournamentStatus,
                "fundAccountId", "setKafka"
        );

        EventDto event = EventDto.builder()
                .id(UUID.randomUUID().toString())
                .message("Change status of Tournament")
                .eventOperationType(EventOperationType.UPDATE_FIELDS)
                .eventTopic(EventProducerModelType.TOURNAMENT)
                .modelId(tournament.getId().toString())
                .modelData(updateFields)
                .createdDate(LocalDateTime.now())
                .build();
        try {
            eventService.sendEvent(event);
        } catch (Exception exc) {
            log.error("Error in handleStatusChange: {}", exc.getMessage());
        }
        //TODO удалить непосредственный вызов изменения данных и разработать обработчик сообщений из Kafka
        tournament.setStatus(newTournamentStatus);
        tournamentService.editTournament(tournament);
    }
}

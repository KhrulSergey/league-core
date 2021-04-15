-- SERIES --
create table if not exists public.tournament_series
(
    id                       bigint not null
        constraint tournament_series_pk primary key,
    name                     varchar(255),
    tournament_id            bigint not null
        constraint fk_tournament_id references public.tournaments (id),
    series_sequence_position int    not null,
    status                   varchar(255),
    type                     varchar(255),
    goal_match_count         int,
    goal_match_rival_count   int,
    start_planned_at         timestamp,
    start_at                 timestamp,
    finished_at              timestamp,
    created_by_league_id     UUID
        constraint fk_created_by_league_id references public.users (league_id),
    modified_by_league_id    UUID
        constraint fk_modified_by_league_id references public.users (league_id),
    created_at               timestamp default now(),
    updated_at               timestamp
);

create sequence if not exists public.tournament_series_id_seq;
create index if not exists tournament_series_id_idx ON public.tournament_series (id);

comment on table public.tournament_series is 'Table for all tournament series on platform';
comment on column public.tournament_series.id is 'Identifier';
comment on column public.tournament_series.tournament_id is 'Reference to tournament';
comment on column public.tournament_series.series_sequence_position is 'Position of all series for current tournament (round number)';
comment on column public.tournament_series.status is 'Current tournament series status';
comment on column public.tournament_series.start_planned_at is 'Planed date of start series with its matches';
comment on column public.tournament_series.start_at is 'Date of start series matches';
comment on column public.tournament_series.finished_at is 'Date of end series matches';

-- MATCHES --
create table if not exists public.tournament_matches
(
    id                    bigint not null
        constraint tournament_match_pk primary key,
    name                  varchar(255),
    series_id             bigint not null
        constraint fk_tournament_series_id references public.tournament_series (id),
    status                varchar(255),
    type                  varchar(255),
    start_planned_at      timestamp,
    start_at              timestamp,
    finished_at           timestamp,
    created_by_league_id  UUID
        constraint fk_created_by_league_id references public.users (league_id),
    modified_by_league_id UUID
        constraint fk_modified_by_league_id references public.users (league_id),
    created_at            timestamp default now(),
    updated_at            timestamp
);

create sequence if not exists public.tournament_matches_id_seq;

comment on table public.tournament_matches is 'Table for all tournament matches on platform';
comment on column public.tournament_matches.id is 'Identifier';
comment on column public.tournament_matches.series_id is 'Reference to tournament series';
comment on column public.tournament_matches.status is 'Current tournament match status';
comment on column public.tournament_matches.start_planned_at is 'Planed date of start match';
comment on column public.tournament_matches.start_at is 'Date of start match';
comment on column public.tournament_matches.finished_at is 'Date of end match';

-- MATCHES RIVAL --
create table if not exists public.tournament_match_rivals
(
    id                    bigint not null
        constraint tournament_match_rival_pk primary key,
    match_id              bigint not null
        constraint fk_tournament_match_id references public.tournament_matches (id),
    team_proposal_id      bigint not null
        constraint fk_team_proposal_id references public.tournament_team_proposal (id),
    status                varchar(255),
    indicators            jsonb,
    place_in_match        int,
    created_by_league_id  UUID
        constraint fk_created_by_league_id references public.users (league_id),
    modified_by_league_id UUID
        constraint fk_modified_by_league_id references public.users (league_id),
    created_at            timestamp default now(),
    updated_at            timestamp
);

create sequence if not exists public.tournament_match_rivals_id_seq;

comment on table public.tournament_match_rivals is 'Table for all tournament match rivals on platform';
comment on column public.tournament_match_rivals.id is 'Identifier';
comment on column public.tournament_match_rivals.match_id is 'Reference to tournament match';
comment on column public.tournament_match_rivals.team_proposal_id is 'Reference to tournament team proposal';
comment on column public.tournament_match_rivals.indicators is 'Matches indicators for rival-team';

-- ADD WINNER TO MATCH --
alter table public.tournament_matches
    add column winner_match_rival_id bigint
        constraint fk_winner_match_rival_id references public.tournament_match_rivals (id);
comment on column public.tournament_matches.winner_match_rival_id is 'Reference of match winner to match rival id';

-- MATCHES RIVAL PARTICIPANT--
create table if not exists public.tournament_match_rival_participant
(
    id                             bigint not null
        constraint tournament_match_rival_participant_pk primary key,
    match_rival_id                 bigint not null
        constraint fk_tournament_match_rival_id references public.tournament_match_rivals (id),
    tournament_team_participant_id bigint not null
        constraint fk_tournament_team_participant_id references public.tournament_team_participants (id),
    status                         varchar(255),
    indicators                     jsonb,
    created_by_league_id           UUID
        constraint fk_created_by_league_id references public.users (league_id),
    modified_by_league_id          UUID
        constraint fk_modified_by_league_id references public.users (league_id),
    created_at                     timestamp default now(),
    updated_at                     timestamp
);

create sequence if not exists public.tournament_match_rival_id_seq;

comment on table public.tournament_match_rival_participant is 'Table for all tournament match rival participant on platform';
comment on column public.tournament_match_rival_participant.id is 'Identifier';
comment on column public.tournament_match_rival_participant.match_rival_id is 'Reference to tournament match rival (team)';
comment on column public.tournament_match_rival_participant.tournament_team_participant_id is 'Reference to team participant on current tournament';
comment on column public.tournament_match_rival_participant.indicators is 'Matches indicators for rival-participant';

-- ROUND --
create table if not exists public.tournament_rounds
(
    id                    bigint not null
        constraint tournament_round_pk primary key,
    name                  varchar(255),
    tournament_id         bigint not null
        constraint fk_round_to_tournament_id references public.tournaments (id),
    round_number          int    not null,
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
create sequence if not exists public.tournament_rounds_id_seq;
create index if not exists tournament_round_id_idx ON public.tournament_rounds (id);

comment on table public.tournament_rounds is 'Table for all tournament rounds on platform';
comment on column public.tournament_rounds.id is 'Identifier';
comment on column public.tournament_rounds.tournament_id is 'Reference to tournament';
comment on column public.tournament_rounds.round_number is 'Round number for current tournament';
comment on column public.tournament_rounds.status is 'Current tournament round status';
comment on column public.tournament_rounds.start_planned_at is 'Planed date of start round with its series of matches';
comment on column public.tournament_rounds.start_at is 'Date of start round';
comment on column public.tournament_rounds.finished_at is 'Date of end round';

-- SERIES RIVAL --
create table if not exists public.tournament_series_rivals
(
    id                    bigint not null
        constraint tournament_series_rival_pk primary key,
    series_id             bigint not null
        constraint fk_tournament_series_id references public.tournament_series (id),
    team_proposal_id      bigint not null
        constraint fk_team_proposal_id references public.tournament_team_proposal (id),
    parent_series_id      bigint
        constraint fk_tournament_series_parents_id references public.tournament_series (id),
    status                varchar(255),
    indicators            jsonb,
    won_place_in_series   int,
    created_by_league_id  UUID
        constraint fk_created_by_league_id references public.users (league_id),
    modified_by_league_id UUID
        constraint fk_modified_by_league_id references public.users (league_id),
    created_at            timestamp default now(),
    updated_at            timestamp
);

create sequence if not exists public.tournament_series_rivals_id_seq;

comment on table public.tournament_series_rivals is 'Table for teams on current tournament series';
comment on column public.tournament_series_rivals.id is 'Identifier';
comment on column public.tournament_series_rivals.series_id is 'Reference to tournament series';
comment on column public.tournament_series_rivals.team_proposal_id is 'Reference to tournament team proposal';
comment on column public.tournament_series_rivals.indicators is 'Matches indicators for rival-team';

-- MODIFY SERIES --
alter table if exists public.tournament_series
    add column IF NOT EXISTS tournament_round_id bigint
        constraint fk_tournament_round_id references public.tournament_rounds (id),
    add column winner_series_rival_id            bigint
        constraint fk_winner_series_rival_id references public.tournament_series_rivals (id),
    drop constraint if exists fk_tournament_id,
    drop column if exists tournament_id,
    drop column if exists series_sequence_position,
    drop column if exists goal_match_count,
    drop column if exists goal_match_rival_count;
comment on column public.tournament_series.tournament_round_id is 'Reference to tournament round id';

-- PARENT SERIES RELATION --
create table if not exists public.tournament_series_parents
(
    series_id        bigint not null
        constraint tournament_series_parents_current_series_id_fk references tournament_series,
    parent_series_id bigint not null
        constraint tournament_series_parents_parent_series_id_fk references tournament_series,
    constraint tournament_series_parents_pkey primary key (series_id, parent_series_id)
);
comment on table public.tournament_series_parents is 'Table for connection from parent to child series';

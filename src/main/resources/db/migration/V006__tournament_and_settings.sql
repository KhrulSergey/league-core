-- TOURNAMENTS --
create table if not exists public.tournaments
(
    id                           bigint       not null
        constraint tournaments_pk primary key,
    name                         varchar(255) not null,
    game_discipline_id           bigint
        constraint fk_game_discipline_id references public.game_disciplines (id),
    game_disciplines_settings_id bigint
        constraint fk_game_disciplines_settings_id references public.game_disciplines_settings (id),
    status                       varchar(255),
    access_type                  varchar(255),
    system_type                  varchar(255),
    discord_channel_name         varchar(500),
    sign_up_start_at             timestamp,
    sign_up_ends_at              timestamp,
    start_planned_at             timestamp,
    created_by_league_id         UUID
        constraint fk_created_by_league_id references public.users (league_id),
    modified_by_league_id        UUID
        constraint fk_modified_by_league_id references public.users (league_id),
    created_at                   timestamp default now(),
    updated_at                   timestamp
);

create sequence if not exists public.tournaments_id_seq;
create index if not exists tournaments_game_discipline_idx ON public.tournaments (game_discipline_id);
create index if not exists tournaments_id_idx ON public.tournaments (id);

comment on table public.tournaments is 'Table for all tournaments on platform';
comment on column public.tournaments.id is 'Identifier';
comment on column public.tournaments.game_discipline_id is 'Reference to game discipline';
comment on column public.tournaments.game_disciplines_settings_id is 'Reference to specific game discipline settings';
comment on column public.tournaments.status is 'Current tournament status';
comment on column public.tournaments.sign_up_start_at is 'Date of start receiving proposals';
comment on column public.tournaments.sign_up_ends_at is 'Date of end receiving proposals';
comment on column public.tournaments.start_planned_at is 'Date of start tournament with its matches';

-- TOURNAMENTS SETTINGS --
create table if not exists public.tournament_settings
(
    id                            bigint not null
        constraint tournament_settings_pk primary key,

    tournament_id                 bigint
        constraint fk_tournament_id references public.tournaments (id),
    organizer_commission          numeric,
    min_team_count                integer,
    max_team_count                integer,
    max_main_participant_count    integer,
    max_reserve_participant_count integer,
    prize_fund                    numeric,
    prize_distribution            jsonb,
    quit_penalty_distribution     jsonb,
    fund_gathering_type           varchar(255),
    created_by_league_id          UUID
        constraint fk_created_by_league_id references public.users (league_id),
    modified_by_league_id         UUID
        constraint fk_modified_by_league_id references public.users (league_id),
    created_at                    timestamp default now(),
    updated_at                    timestamp
);
create sequence if not exists public.tournament_settings_id_seq;

comment on table public.tournament_settings is 'Table for all team_participants on platform';
comment on column public.tournament_settings.id is 'Identifier';
comment on column public.tournament_settings.tournament_id is 'Reference to tournament';


-- TOURNAMENT ORGANIZERS--
create table if not exists public.tournament_organizers
(
    id                    bigint not null
        constraint tournament_organizers_pk primary key,
    tournament_id         bigint
        constraint fk_tournament_id references public.tournaments (id),
    league_id             UUID
        constraint fk_league_id references public.users (league_id),
    status                varchar(255),
    privilege_list        jsonb,
    created_by_league_id  UUID
        constraint fk_created_by_league_id references public.users (league_id),
    modified_by_league_id UUID
        constraint fk_modified_by_league_id references public.users (league_id),
    created_at            timestamp default now(),
    updated_at            timestamp
);
create sequence if not exists public.tournament_organizers_id_seq;

comment on table public.tournament_organizers is 'Table for all tournament organizers';
comment on column public.tournament_organizers.id is 'Identifier';
comment on column public.tournament_organizers.league_id is 'Reference to user leagueId';

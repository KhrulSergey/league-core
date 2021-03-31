-- GAMES --
create table if not exists public.game_disciplines
(
    id                    bigint        not null
        constraint game_disciplines_pk primary key,
    name                  varchar(255)  not null unique,
    description           varchar(2000) not null,
    logo_file_name        varchar(500),
    is_active             bool          not null default true,
    created_by_league_id  UUID
        constraint fk_created_by_league_id references public.users (league_id),
    modified_by_league_id UUID
        constraint fk_modified_by_league_id references public.users (league_id),
    created_at            timestamp              default now(),
    updated_at            timestamp
);

create sequence if not exists public.game_disciplines_id_seq;
create index if not exists game_disciplines_id_idx ON public.game_disciplines (id);

comment on table public.game_disciplines is 'Table for all game disciplines on platform';
comment on column public.game_disciplines.id is 'Identifier of the game';
comment on column public.game_disciplines.name is 'Name of the game';

-- GAMES SETTINGS --
CREATE EXTENSION IF NOT EXISTS hstore;

create table if not exists public.game_disciplines_settings
(
    id                      bigint       not null
        constraint game_disciplines_settings_pk primary key,
    name                    varchar(255) not null unique,
    is_primary              bool         not null default false,
    game_discipline_id      bigint
        constraint fk_game_discipline_id references public.game_disciplines (id),
    game_optimal_indicators hstore,
    created_by_league_id    UUID
        constraint fk_created_by_league_id references public.users (league_id),
    modified_by_league_id   UUID
        constraint fk_modified_by_league_id references public.users (league_id),
    created_at              timestamp             default now(),
    updated_at              timestamp
);
create sequence if not exists public.game_disciplines_settings_id_seq;

comment on table public.game_disciplines_settings is 'Table for all game settings on platform';
comment on column public.game_disciplines_settings.id is 'Identifier';
comment on column public.game_disciplines_settings.game_optimal_indicators is 'Hash map of indicators with optimal values';

-- GAMES SETTINGS --
DROP EXTENSION IF EXISTS hstore;

alter table if exists public.game_disciplines_settings
    add column IF NOT EXISTS match_rival_count bigint default 2;

alter table if exists public.tournament_settings
    add column IF NOT EXISTS match_count_per_series bigint default 3;

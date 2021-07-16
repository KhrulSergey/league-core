-- TOURNAMENT SETTINGS ADD BRACKETS GENERATION TUNING PARAMS --
alter table if exists public.tournament_settings
    add column if not exists is_auto_finish_round_enabled  boolean default true,
    add column if not exists is_generation_round_enabled   boolean default true,
    add column if not exists is_sequential_series_enabled  boolean default false,
    add column if not exists is_auto_finish_series_enabled boolean default true,
    add column if not exists is_generation_series_enabled  boolean default true;

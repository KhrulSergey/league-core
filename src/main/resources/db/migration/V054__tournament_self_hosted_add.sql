-- MODIFY TOURNAMENT SETTINGS --
alter table if exists public.tournament_settings
    add column IF NOT EXISTS score_distribution_within_rivals jsonb;

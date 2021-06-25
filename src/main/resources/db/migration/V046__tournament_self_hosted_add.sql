-- MODIFY TOURNAMENT SETTINGS --
alter table if exists public.tournament_settings
    add column IF NOT EXISTS self_hosted boolean default false;

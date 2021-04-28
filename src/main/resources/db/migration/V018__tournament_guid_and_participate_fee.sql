-- TOURNAMENT ADD GUID--
alter table if exists public.tournaments
    add column if not exists core_id uuid;

UPDATE public.tournaments
SET core_id = uuid_in(md5(random()::text || clock_timestamp()::text)::cstring);

-- TOURNAMENT SETTINGS ADD PARTICIPATE FEE--
alter table if exists public.tournament_settings
    add column if not exists participation_fee numeric default 0.0;

-- TEAM ADD GUID--
alter table if exists team_management.teams
    add column if not exists core_id uuid;

UPDATE team_management.teams
SET core_id = uuid_in(md5(random()::text || clock_timestamp()::text)::cstring);

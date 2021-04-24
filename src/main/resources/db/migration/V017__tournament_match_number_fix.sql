-- TOURNAMENT MATCH NUMBER ADD --
alter table if exists public.tournament_matches
    add column if not exists match_number_in_series int default 1;

-- TOURNAMENT FINISHED DATE ADD --
alter table if exists public.tournaments
    add column if not exists finished_at timestamp;

-- MODIFY MATCH --
alter table if exists public.tournament_matches
    add column IF NOT EXISTS has_no_winner boolean default false;

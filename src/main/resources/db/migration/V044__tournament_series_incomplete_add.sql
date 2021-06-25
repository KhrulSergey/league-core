-- MODIFY SERIES --
alter table if exists public.tournament_series
    add column IF NOT EXISTS is_incomplete boolean default false,
    add column IF NOT EXISTS has_no_winner boolean default false;


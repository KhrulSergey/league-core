-- TOURNAMENT ADD force_finish --
alter table if exists public.tournaments
    add column if not exists is_forced_finished boolean default false;


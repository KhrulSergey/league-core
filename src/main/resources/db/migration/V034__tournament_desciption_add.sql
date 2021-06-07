-- TOURNAMENT ADD DESCIPTION --
alter table if exists public.tournaments
    add column if not exists description text;

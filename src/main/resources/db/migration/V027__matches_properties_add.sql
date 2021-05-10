-- MATCHES ADD PROPERTIES--
alter table if exists public.tournament_matches
    add column if not exists match_properties jsonb;

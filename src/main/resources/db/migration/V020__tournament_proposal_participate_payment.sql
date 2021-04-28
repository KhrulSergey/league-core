-- TOURNAMENT PROPOSAL ADD participate_payment --
alter table if exists public.tournament_team_proposal
    add column if not exists participate_payment_list jsonb;

-- TOURNAMENT PROPOSAL change check-in behavior --

alter table if exists public.tournament_team_proposal
     alter column confirmed set default false;

UPDATE public.tournament_team_proposal
SET confirmed = false
WHERE confirmed is null;

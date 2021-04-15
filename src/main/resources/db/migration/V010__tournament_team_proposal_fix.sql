-- ADD TOURNAMENT REF TO tournament_team_proposal --
alter table public.tournament_team_proposal
    add column IF NOT EXISTS tournament_id bigint
        constraint fk_tournament_id references public.tournaments (id);
comment on column public.tournament_team_proposal.tournament_id is 'Reference to tournament id';

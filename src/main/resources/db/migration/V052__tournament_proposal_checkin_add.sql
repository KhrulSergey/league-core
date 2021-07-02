-- TOURNAMENT PROPOSAL ADD participate_payment --
alter table if exists public.tournament_team_proposal
    add column if not exists confirmed boolean;

comment on column public.tournament_team_proposal.confirmed is 'Sign of confirmation to participate in tournament (check-in)';

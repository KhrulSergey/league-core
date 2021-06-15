-- TOURNAMENT ADD PARTICIPANT TYPE --
alter table league_core.public.tournaments
    add column if not exists participant_type varchar(255) default 'TEAM';

-- TOURNAMENT ADD PARTICIPANT TYPE --
alter table league_core.public.tournament_team_proposal
    add column if not exists participant_type varchar(255) default 'TEAM';


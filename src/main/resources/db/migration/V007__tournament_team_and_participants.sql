-- TEAM PROPOSAL TO TOURNAMENT --
create table if not exists public.tournament_team_proposal
(
    id         bigint not null
        constraint teams_pk primary key,
    team_id    bigint
        constraint fk_team_id references team_management.teams (id),
    status     varchar(255),
    type       varchar(255),
    created_at timestamp default now(),
    updated_at timestamp
);

create sequence if not exists public.tournament_team_proposal_id_seq;
create index if not exists tournament_team_proposal_id_idx ON public.tournament_team_proposal (id);

comment on table public.tournament_team_proposal is 'Table for all team proposals to tournaments on platform';
comment on column public.tournament_team_proposal.id is 'Identifier';

-- TEAM PARTICIPANT IS USED IN PROPOSAL TO TOURNAMENT  --
create table if not exists public.tournament_team_participants
(
    id                          bigint not null
        constraint tournament_team_participants_pk primary key,
    tournament_team_proposal_id bigint
        constraint fk_tournament_team_proposal_id references public.tournament_team_proposal (id),
    team_participant_id         bigint
        constraint fk_team_participant_id references team_management.team_participants (id),
    league_id                   UUID
        constraint fk_league_id references public.users (league_id),
    status                      varchar(255),
    created_at                  timestamp default now(),
    updated_at                  timestamp
);
create sequence if not exists public.tournament_team_participants_id_seq;

comment on table public.tournament_team_participants is 'Table for all team participants used in proposal to tournament';
comment on column public.tournament_team_participants.id is 'Identifier';
comment on column public.tournament_team_participants.league_id is 'Reference to user leagueId';
comment on column public.tournament_team_participants.team_participant_id is 'Reference to team participant entries';

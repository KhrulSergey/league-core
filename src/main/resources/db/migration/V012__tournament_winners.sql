-- TOURNAMENT WINNER PARTICIPANT--
create table if not exists public.tournament_winners
(
    id                    bigint not null
        constraint tournament_winners_pk primary key,
    tournament_id         bigint not null
        constraint fk_tournament_id references public.tournaments (id),
    team_proposal_id      bigint not null
        constraint fk_team_proposal_id references public.tournament_team_proposal (id),
    winner_place          int    not null,
    created_by_league_id  UUID
        constraint fk_created_by_league_id references public.users (league_id),
    modified_by_league_id UUID
        constraint fk_modified_by_league_id references public.users (league_id),
    created_at            timestamp default now(),
    updated_at            timestamp
);

create sequence if not exists public.tournament_winners_id_seq;

comment on table public.tournament_winners is 'Table for all tournament winners';
comment on column public.tournament_winners.id is 'Identifier';
comment on column public.tournament_winners.tournament_id is 'Reference to tournament';
comment on column public.tournament_winners.team_proposal_id is 'Reference to team proposal (team) on current tournament';

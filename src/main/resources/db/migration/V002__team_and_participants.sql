-- NEW SCHEMA --
create schema if not exists team_management;

-- TEAMS --
create table if not exists team_management.teams
(
    id                  bigint       not null
        constraint teams_pk primary key,
    name                varchar(255) not null,
    team_logo_file_name varchar(255),
    status              varchar(255),
    created_at          timestamp default now(),
    updated_at          timestamp
);

create sequence if not exists team_management.teams_id_seq;
create index if not exists team_name_idx ON team_management.teams (name);
create index if not exists team_id_idx ON team_management.teams (id);

comment on table team_management.teams is 'Table for all teams on platform';
comment on column team_management.teams.id is 'Identifier';

-- team_participants --
create table if not exists team_management.team_participants
(
    id         bigint not null
        constraint team_participants_pk primary key,
    league_id  UUID
        constraint fk_league_id references public.users (league_id),
    team_id    bigint
        constraint fk_team_id references team_management.teams (id),
    join_at    timestamp,
    deleted_at timestamp,
    status     varchar(255),
    created_at timestamp default now(),
    updated_at timestamp
);
create sequence if not exists team_management.team_participants_id_seq;

comment on table team_management.team_participants is 'Table for all team_participants on platform';
comment on column team_management.team_participants.id is 'Identifier';
comment on column team_management.team_participants.league_id is 'Reference to user leagueId';

-- ADD CAPTAIN TO TEAMS --
alter table team_management.teams
    add column captain_id bigint
        constraint fk_captain_id references team_management.team_participants (id);
comment on column team_management.teams.captain_id is 'Reference to user leagueId';

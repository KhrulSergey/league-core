-- TEAM INVITE REQUEST --
create table if not exists team_management.team_invite_requests
(
    id                     bigint        not null
        constraint team_invite_requests_pk primary key,
    invite_token           varchar(1000) not null unique,
    invited_user_league_id UUID
        constraint fk_invited_user_league_id references public.users (league_id),
    team_id                bigint
        constraint fk_team_id references team_management.teams (id),
    participant_creator_id bigint
        constraint fk_creator_participant_id references team_management.team_participants (id),
    expiration             timestamp,
    status                 varchar(255),
    participant_applied_id bigint
        constraint fk_applied_participant_id references team_management.team_participants (id),
    created_at             timestamp default now(),
    updated_at             timestamp
);

create sequence if not exists team_management.team_invite_request_id_seq;
create index if not exists team_invite_requests_invite_hash_idx ON team_management.team_invite_requests (invite_token);

comment on table team_management.team_invite_requests is 'Table for all invite request for join teams';
comment on column team_management.team_invite_requests.id is 'Identifier';
comment on column team_management.team_invite_requests.invite_token is 'Unique token link to join the team';
comment on column team_management.team_invite_requests.invited_user_league_id is 'Invited user with reference to his leagueId';
comment on column team_management.team_invite_requests.participant_creator_id is 'Creator (participant) of invitation link';
comment on column team_management.team_invite_requests.participant_applied_id is 'Created participant (successfully applied invitation)';

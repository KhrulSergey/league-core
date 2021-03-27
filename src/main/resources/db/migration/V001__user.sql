--USERS--
create table if not exists public.users
(
    id               bigint not null
        constraint users_pk primary key,
    league_id        UUID   not null unique,
    username         varchar(255),
    avatar_file_name varchar(500),
    status           varchar(255),
    created_at       timestamp,
    updated_at       timestamp
);
comment on table public.users is 'Table for all users';
comment on column public.users.id is 'Identifier';
comment on column public.users.league_id is 'League Identifier';

create sequence if not exists users_id_seq;
create index if not exists users_idx ON public.users (league_id);

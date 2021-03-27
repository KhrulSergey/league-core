-- SESSION --
create table if not exists public.session
(
    id               bigint not null
        constraint session_pk primary key,
    token            varchar(255),
    freetonleague_id uuid,
    expiration       timestamp,
    auth_provider    varchar(100),
    created_at       timestamp default now(),
    updated_at       timestamp
);

comment on table public.session is 'Table for users sessions';
comment on column public.session.id is 'Identifier';
comment on column public.session.freetonleague_id is 'Reference to User';
comment on column public.session.token is 'Session token';

create sequence if not exists session_id_seq;
create index if not exists session_idx ON public.session (token);

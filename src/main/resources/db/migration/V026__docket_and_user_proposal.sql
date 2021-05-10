-- DOCKET --
create table if not exists public.dockets
(
    id                    bigint       not null
        constraint dockets_pk primary key,
    name                  varchar(255) not null,
    core_id               uuid,
    description           text,
    text_label            varchar(1000),
    status                varchar(255),
    access_type           varchar(255),
    participation_fee     numeric,
    sign_up_start_at      timestamp,
    sign_up_ends_at       timestamp,
    start_planned_at      timestamp,
    finished_at           timestamp,
    created_by_league_id  UUID
        constraint fk_created_by_league_id references public.users (league_id),
    modified_by_league_id UUID
        constraint fk_modified_by_league_id references public.users (league_id),
    created_at            timestamp default now(),
    updated_at            timestamp
);

create sequence if not exists public.dockets_id_seq;
create index if not exists dockets_id_idx ON public.dockets (id);

comment on table public.dockets is 'Table for all docket (universal list) on platform';
comment on column public.dockets.id is 'Identifier of docket';
comment on column public.dockets.core_id is 'Unique identifier of docket';
comment on column public.dockets.description is 'Description for docket';
comment on column public.dockets.text_label is 'Text label (mark, question) for docket tournament status';

-- User participation in docket --
create table if not exists public.docket_proposals
(
    id                       bigint not null
        constraint docket_proposals_pk primary key,
    league_id                UUID
        constraint fk_league_id references public.users (league_id),
    docket_id                bigint
        constraint fk_docket_id references public.dockets (id),
    text_label_answer        varchar(1000),
    state                    varchar(255),
    participate_payment_list jsonb,
    created_by_league_id     UUID
        constraint fk_created_by_league_id references public.users (league_id),
    modified_by_league_id    UUID
        constraint fk_modified_by_league_id references public.users (league_id),
    created_at               timestamp default now(),
    updated_at               timestamp
);

create sequence if not exists public.docket_proposals_id_seq;

comment on table public.docket_proposals is 'Table for all user proposals to dockets on platform';
comment on column public.docket_proposals.id is 'Identifier';
comment on column public.docket_proposals.text_label_answer is 'Answer of user to docket.textLabel if it was notBlank';
comment on column public.docket_proposals.participate_payment_list is 'Reference to team participant entries';

create table if not exists public.docket_promos
(
    id         bigint            not null
        constraint docket_promos_pk primary key,
    max_usages int               not null,
    promo_code varchar(5) unique not null,
    enabled    boolean           not null,
    created_at timestamp default now(),
    updated_at timestamp
);

create sequence if not exists public.docket_promos_id_seq;
create index if not exists docket_promos_promo_code_idx ON public.docket_promos (promo_code);

create table if not exists public.docket_promos_usages
(
    id         bigint not null
        constraint docket_promos_usages_pk primary key,
    promo_id   bigint
        constraint fk_docket_promos_usages_promo_id references public.docket_promos (id),
    user_id    bigint
        constraint fk_docket_promos_usages_user_id references public.users (id),
    created_at timestamp default now(),
    updated_at timestamp
);

create sequence if not exists public.docket_promos_usages_id_seq;

alter table if exists public.dockets
    add column if not exists promo_id bigint
        constraint fk_dockets_docket_promos_id references public.docket_promos (id);

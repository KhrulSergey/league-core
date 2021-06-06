-- NEWS --
create table if not exists public.news
(
    id                    bigint       not null
        constraint news_pk primary key,
    title                 varchar(255) not null,
    theme                 varchar(255),
    image_url             varchar(500),
    description           text,
    status                varchar(255),
    tags                  jsonb,
    created_by_league_id  UUID
        constraint fk_created_by_league_id references public.users (league_id),
    modified_by_league_id UUID
        constraint fk_modified_by_league_id references public.users (league_id),
    created_at            timestamp default now(),
    updated_at            timestamp
);

create sequence if not exists public.news_id_seq;

comment on table public.news is 'Table for all news';
comment on column public.news.id is 'Identifier';
comment on column public.news.title is 'Title of news';

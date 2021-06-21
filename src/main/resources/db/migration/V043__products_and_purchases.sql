-- PRODUCTS --
create table if not exists public.products
(
    id                     bigint       not null
        constraint products_pk primary key,
    name                   varchar(255) not null,
    core_id                uuid,
    description            text,
    image_url              varchar(255),
    product_parameters     jsonb,
    status                 varchar(255),
    access_type            varchar(255),
    cost                   numeric,
    quantity_in_stock      numeric,
    possible_quantity_step numeric,
    archived_at            timestamp,
    created_by_league_id   UUID
        constraint fk_created_by_league_id references public.users (league_id),
    modified_by_league_id  UUID
        constraint fk_modified_by_league_id references public.users (league_id),
    created_at             timestamp default now(),
    updated_at             timestamp
);

create sequence if not exists public.products_id_seq;
create index if not exists products_id_idx ON public.products (id);

comment on table public.products is 'Table for all products on platform';
comment on column public.products.id is 'Identifier of product';
comment on column public.products.core_id is 'Unique identifier of product';
comment on column public.products.description is 'Description for product';
comment on column public.products.product_parameters is 'Detailed parameters (text labels) for product';

-- User purchase of products --
create table if not exists public.product_purchases
(
    id                          bigint not null
        constraint product_purchases_pk primary key,
    core_id                     uuid,
    league_id                   UUID
        constraint fk_league_id references public.users (league_id),
    product_id                  bigint
        constraint fk_products_id references public.products (id),
    purchase_quantity           bigint,
    purchase_total_amount       bigint,
    selected_product_parameters jsonb,
    state                       varchar(255),
    type                        varchar(255),
    buyer_comment               text,
    manager_comment             text,
    purchase_payment_list       jsonb,
    created_by_league_id        UUID
        constraint fk_created_by_league_id references public.users (league_id),
    modified_by_league_id       UUID
        constraint fk_modified_by_league_id references public.users (league_id),
    created_at                  timestamp default now(),
    updated_at                  timestamp
);

create sequence if not exists public.product_purchases_id_seq;
create index if not exists products_id_idx ON public.products (id);

comment on table public.product_purchases is 'Table for all user purchase of product';
comment on column public.product_purchases.id is 'Identifier';
comment on column public.product_purchases.selected_product_parameters is 'Selection (answer) of docket.productParameters if it was notBlank';
comment on column public.product_purchases.purchase_payment_list is 'Reference to financial unit transaction';

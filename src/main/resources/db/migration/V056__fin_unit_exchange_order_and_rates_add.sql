-- TRANSACTION EDIT NAME TO DESCRIPTION --
DO
$$
    BEGIN
        IF EXISTS(SELECT *
                  FROM information_schema.columns
                  WHERE table_schema = 'league_finance'
                    and table_name = 'transactions'
                    and column_name = 'name')
        THEN
            ALTER TABLE "league_finance"."transactions"
                RENAME COLUMN "name" TO "description";
        END IF;
    END
$$;

-- EXCHANGE RATIO --
create table if not exists league_finance.exchange_ratios
(
    id                         bigint not null
        constraint exchange_ratio_pk primary key,
    guid                       uuid   not null unique,
    provider                   varchar(255),
    currency_pair              varchar(255),
    currency_to_buy            varchar(255),
    currency_to_sell           varchar(255),
    ratio                      numeric,
    exchange_currency_rate_raw jsonb,
    status                     varchar(255),
    payment_url                varchar(500),
    client_account_guid        UUID
        constraint fk_exchange_order_client_account_guid references league_finance.accounts (guid),
    payment_transaction_guid   UUID
        constraint fk_exchange_order_transactions_guid references league_finance.transactions (guid),
    expired_at                 timestamp,
    created_by_league_id       UUID,
    modified_by_league_id      UUID,
    created_at                 timestamp default now(),
    updated_at                 timestamp
);
create sequence if not exists league_finance.exchange_ratio_id_seq;
create index if not exists exchange_ratio_guid_idx ON league_finance.exchange_ratios (guid);

comment on table league_finance.exchange_ratios is 'Table for all exchange currency ratio on platform';
comment on column league_finance.exchange_ratios.id is 'Identifier';
comment on column league_finance.exchange_ratios.guid is 'Unique string-guid for exchange ratio';
comment on column league_finance.exchange_ratios.provider is 'Provider of exchange ratio information';
comment on column league_finance.exchange_ratios.currency_pair is 'Currency pair for exchange ratio';
comment on column league_finance.exchange_ratios.status is 'Status of exchange ratio entry';
comment on column league_finance.exchange_ratios.expired_at is 'Date and time of exchange ratio expiration';

-- PARENT EXCHANGE RATIO RELATIONS --
create table if not exists league_finance.exchange_ratio_parents
(
    current_ratio_id bigint not null
        constraint exchange_ratio_parents_current_ratio_id_fk references league_finance.exchange_ratios (id),
    parent_ratio_id  bigint not null
        constraint exchange_ratio_parents_parent_ratio_id_fk references league_finance.exchange_ratios (id),
    constraint exchange_ratio_parents_pkey primary key (current_ratio_id, parent_ratio_id)
);
comment on table league_finance.exchange_ratio_parents is 'Table for connection from parent to child exchange ratios (currency exchange sequence)';

-- EXCHANGE ORDER --
create table if not exists league_finance.exchange_orders
(
    id                       bigint not null
        constraint exchange_order_pk primary key,
    guid                     uuid   not null unique,
    currency_to_buy          varchar(255),
    amount_to_buy            numeric,
    currency_to_sell         varchar(255),
    amount_to_sell           numeric,
    exchange_ratio_guid      UUID
        constraint fk_exchange_order_exchange_ratio_guid references league_finance.exchange_ratios (guid),
    status                   varchar(255),
    payment_url              varchar(500),
    payment_gateway          varchar(255),
    client_account_guid      UUID
        constraint fk_exchange_order_client_account_guid references league_finance.accounts (guid),
    payment_transaction_guid UUID
        constraint fk_exchange_order_transactions_guid references league_finance.transactions (guid),
    payment_invoice_raw      jsonb,
    expired_at               timestamp,
    finished_at              timestamp,
    created_by_league_id     UUID,
    modified_by_league_id    UUID,
    created_at               timestamp default now(),
    updated_at               timestamp
);
create sequence if not exists league_finance.exchange_order_id_seq;
create index if not exists exchange_order_guid_idx ON league_finance.exchange_orders (guid);

comment on table league_finance.exchange_orders is 'Table for all exchange orders on platform';
comment on column league_finance.exchange_orders.id is 'Identifier';
comment on column league_finance.exchange_orders.guid is 'Unique string-guid for exchange order';
comment on column league_finance.exchange_orders.exchange_ratio_guid is 'Reference to exchange ratio entry';
comment on column league_finance.exchange_orders.currency_to_buy is 'Currency from order to buy';
comment on column league_finance.exchange_orders.currency_to_sell is 'Currency from order to sell';
comment on column league_finance.exchange_orders.status is 'Status of exchange order';
comment on column league_finance.exchange_orders.client_account_guid is 'Reference to client account entry';
comment on column league_finance.exchange_orders.payment_transaction_guid is 'Reference to transfer transaction entry';
comment on column league_finance.exchange_orders.expired_at is 'Date and time of exchange ratio expiration';

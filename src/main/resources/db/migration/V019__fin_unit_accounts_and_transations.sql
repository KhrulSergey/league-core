-- NEW FIN SCHEMA --
create schema if not exists league_finance;
comment on schema league_finance is 'FreeTon League finance data';

-- ACCOUNT HOLDERS --
create table if not exists league_finance.account_holders
(
    id                    bigint not null
        constraint account_holders_pk primary key,
    guid                  uuid   not null unique,
    holder_external_guid  uuid,
    holder_type           varchar(255),
    holder_name           varchar(255),
    created_by_league_id  UUID,
    modified_by_league_id UUID,
    created_at            timestamp default now(),
    updated_at            timestamp
);

create sequence if not exists league_finance.account_holders_id_seq;
create index if not exists account_holders_guid_idx ON league_finance.account_holders (guid);
create index if not exists account_holders_external_guid_idx ON league_finance.account_holders (holder_external_GUID);

comment on table league_finance.account_holders is 'Table for all account holders (users and entities) on platform';
comment on column league_finance.account_holders.id is 'Identifier';
comment on column league_finance.account_holders.guid is 'Unique string-guid for account holder on platform';
comment on column league_finance.account_holders.holder_external_GUID is 'Reference to account holder native guid';
comment on column league_finance.account_holders.holder_type is 'Account holder type';

-- ACCOUNT --
create table if not exists league_finance.accounts
(
    id                           bigint not null
        constraint accounts_pk primary key,
    guid                         uuid   not null unique,
    holder_guid                  uuid   not null
        constraint fk_accounts_holder_guid references league_finance.account_holders (guid),
    name                         varchar(255),
    amount                       numeric,
    type                         varchar(255),
    status                       varchar(255),
    external_address             varchar(255),
    external_bank_type           varchar(255),
    external_bank_last_update_at timestamp,
    created_by_league_id         UUID,
    modified_by_league_id        UUID,
    created_at                   timestamp default now(),
    updated_at                   timestamp
);

create sequence if not exists league_finance.accounts_id_seq;
create index if not exists accounts_guid_idx ON league_finance.accounts (guid);

comment on table league_finance.accounts is 'Table for all financial accounts on platform';
comment on column league_finance.accounts.id is 'Identifier';
comment on column league_finance.accounts.guid is 'Unique string-guid for account on platform';
comment on column league_finance.accounts.holder_GUID is 'Reference to Account Holder (inner guid in finance unit)';


-- TRANSACTIONS --
create table if not exists league_finance.transactions
(
    id                    bigint not null
        constraint transactions_pk primary key,
    guid                  uuid   not null unique,
    amount                numeric,
    account_source_guid   UUID
        constraint fk_transactions_account_source_guid references league_finance.accounts (guid),
    account_target_guid   UUID   not null
        constraint fk_transactions_account_target_guid references league_finance.accounts (guid),
    name                  varchar(255),
    type                  varchar(255),
    status                varchar(255),
    template_type         varchar(255),
    created_by_league_id  UUID,
    modified_by_league_id UUID,
    created_at            timestamp default now(),
    updated_at            timestamp
);

alter table if exists league_finance.transactions
    add column if not exists parent_transaction_guid uuid
        constraint fk_transactions_parent_transaction_guid references league_finance.transactions (guid);

create sequence if not exists league_finance.transactions_id_seq;
create index if not exists transactions_guid_idx ON league_finance.transactions (guid);

comment on table league_finance.transactions is 'Table for all tournament matches on platform';
comment on column league_finance.transactions.id is 'Identifier';
comment on column league_finance.transactions.guid is 'Unique string-guid for transaction on platform';
comment on column league_finance.transactions.account_source_guid is 'Reference to financial Account as source of fund (inner guid of account in finance unit)';
comment on column league_finance.transactions.account_target_guid is 'Reference to financial Account as target for fund transfer (inner guid of account in finance unit)';
comment on column league_finance.transactions.parent_transaction_guid is 'Reference to parent Transaction if it exists (inner guid of transaction in finance unit)';

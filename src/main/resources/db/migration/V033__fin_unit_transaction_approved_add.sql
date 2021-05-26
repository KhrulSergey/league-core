-- TRANSACTION ADD APPROVE --
alter table if exists league_finance.transactions
    add column if not exists approved_by_league_id UUID,
    add column if not exists finished_at           timestamp;

alter table if exists league_finance.accounts
    alter column holder_guid drop not null,
    add column if not exists is_not_tracking boolean;

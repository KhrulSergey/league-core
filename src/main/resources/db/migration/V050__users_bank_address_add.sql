-- USER BANK ADDRESS ADD --
alter table if exists users
    add column if not exists steam_id             varchar(255),
    add column if not exists bank_account_address varchar(600);


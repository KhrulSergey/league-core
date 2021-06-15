alter table if exists users
    add column if not exists parameters jsonb;

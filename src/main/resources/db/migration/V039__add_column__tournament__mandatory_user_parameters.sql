alter table if exists tournaments
    add column if not exists mandatory_user_parameters jsonb;

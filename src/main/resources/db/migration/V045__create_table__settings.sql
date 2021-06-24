create table if not exists public.settings
(
    id          bigint       not null
        constraint settings_pk primary key,
    key         varchar(255) not null unique,
    value       varchar(255),
    description varchar(2048),
    created_at  timestamp,
    updated_at  timestamp
);

create sequence if not exists settings_id_seq;

insert into settings (id, key, value, description, created_at, updated_at)
VALUES (nextval('settings_id_seq'), 'TON_TO_UC_EXCHANGE_RATE_KEY', '1', 'Курс обмена TON на UC', now(), now())

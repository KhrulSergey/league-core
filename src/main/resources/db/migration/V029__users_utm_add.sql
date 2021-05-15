-- USER NAME AND UTM ADD --
alter table if exists users
    add column if not exists name       varchar(255),
    add column if not exists email      varchar(600),
    add column if not exists utm_source varchar(255),
    add column if not exists is_hidden  boolean default false;

UPDATE public.users
SET is_hidden = true,
    status    = 'ACTIVE'
WHERE status = 'HIDDEN';

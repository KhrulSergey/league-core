--USER discord ID--
alter table public.users
    add column if not exists
        discord_id varchar(255);

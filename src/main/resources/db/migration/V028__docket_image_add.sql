-- DOCKET ADD IMAGE --
alter table if exists public.dockets
    add column if not exists image_url varchar(500);

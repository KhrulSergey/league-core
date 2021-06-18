-- DOCKET ADD MAX PARTICIPATION FEE  --
alter table if exists public.dockets
    add column if not exists max_participation_fee int,
    add column if not exists system_type           varchar(255) default 'DEFAULT';

alter table if exists public.docket_proposals
    add column if not exists participation_fee int;

-- DOCKET ADD MAX PROPOSAL COUNT SETTING ADD --
alter table if exists dockets
    add column if not exists max_proposal_count int;

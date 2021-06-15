-- TEAM ADD IS_VIRTUAL --
alter table league_core.team_management.teams
    add column if not exists is_virtual boolean default false;

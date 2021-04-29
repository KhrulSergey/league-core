-- Team create unique index --
drop index if exists team_name_idx;

create unique index if not exists team_name_idx on team_management.teams (name);


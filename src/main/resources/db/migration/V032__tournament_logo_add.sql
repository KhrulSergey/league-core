-- TOURNAMENT ADD LOGO --
alter table if exists tournaments
    add column if not exists logo_file_name varchar(255);

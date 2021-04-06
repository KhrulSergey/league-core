-- GAMES --
truncate table game_disciplines_settings cascade;

alter table game_disciplines_settings
alter column game_optimal_indicators type jsonb using game_optimal_indicators::jsonb;

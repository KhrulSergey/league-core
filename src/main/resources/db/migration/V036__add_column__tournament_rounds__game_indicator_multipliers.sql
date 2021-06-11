alter table if exists tournament_rounds
    add column if not exists game_indicator_multipliers jsonb;

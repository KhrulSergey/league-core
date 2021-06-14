-- GAMES DISCIPLINE SETTINGS --
alter table league_core.public.game_disciplines_settings
    add column if not exists series_rival_kick_off_default_count int default 1;

-- TOURNAMENT SETTINGS --
alter table league_core.public.tournament_settings
    add column if not exists tournament_round_settings_list jsonb;

-- TOURNAMENT ROUND --
alter table league_core.public.tournament_rounds
    add column if not exists is_last boolean;


-- SERIES PARENTS CHANGE FIELDS NAMES --
DO
$$
    BEGIN
        IF EXISTS(SELECT *
                  FROM information_schema.columns
                  WHERE table_name = 'tournament_series_parents'
                    and column_name = 'series_id')
        THEN
            ALTER TABLE "public"."tournament_series_parents"
                RENAME COLUMN "parent_series_id" TO "child_series_id";
            ALTER TABLE "public"."tournament_series_parents"
                RENAME COLUMN "series_id" TO "parent_series_id";
        END IF;
    END
$$;

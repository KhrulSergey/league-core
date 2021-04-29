-- CHANGE TOURNAMENT DISCORD CHANNEL NAME AND TYPE --

UPDATE public.tournaments
SET discord_channel_name = floor(random() * 100000000000000);

DO
$$
    BEGIN
        IF EXISTS(SELECT *
                  FROM information_schema.columns
                  WHERE table_name = 'tournaments'
                    and column_name = 'discord_channel_name')
        THEN
            ALTER TABLE "public"."tournaments"
                RENAME COLUMN "discord_channel_name" TO "discord_channel_id";
        END IF;
    END
$$;



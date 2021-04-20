-- Change TOURNAMENT TEAM PROPOSAL column name --

DO
$$
    BEGIN
        IF EXISTS(SELECT *
                  FROM information_schema.columns
                  WHERE table_name = 'tournament_team_proposal'
                    and column_name = 'status')
        THEN
            ALTER TABLE "public"."tournament_team_proposal"
                RENAME COLUMN "status" TO "state";
        END IF;
    END
$$;

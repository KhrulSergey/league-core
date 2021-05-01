-- Service user --
DO
$$
    BEGIN
        IF NOT EXISTS(SELECT *
                      FROM public.users
                      WHERE username = 'SERVICE_QPhMMkF4GFEL5Vn6F45PHSaC1496')
        THEN
            INSERT INTO public.users(id, league_id, username, status, created_at, updated_at)
            VALUES (nextval('users_id_seq'), uuid_in(md5(random()::text || clock_timestamp()::text)::cstring),
                    'SERVICE_QPhMMkF4GFEL5Vn6F45PHSaC1496', 'HIDDEN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

--Role with ID 4 because: INSERT INTO public.roles(id, name) VALUES (4, 'EXTERNAL_SERVICE') ON CONFLICT DO NOTHING;
            INSERT INTO public.user_roles (user_id, role_id)
            select id, 4
            FROM public.users
            WHERE username like 'SERVICE_QPhMMkF4GFEL5Vn6F45PHSaC1496'
            ON CONFLICT DO NOTHING;
        END IF;
    END
$$;




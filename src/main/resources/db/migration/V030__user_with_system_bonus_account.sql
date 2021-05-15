-- System Role --
INSERT INTO public.roles(id, name)
VALUES (5, 'INNER_SYSTEM')
ON CONFLICT DO NOTHING;

-- Service user --
DO
$$
    BEGIN
        IF NOT EXISTS(SELECT *
                      FROM public.users
                      WHERE username = 'SYSTEM_KdnIgwRY0qzedOZ6R2X6T9GtNDz')
        THEN
            INSERT INTO public.users(id, league_id, username, is_hidden, status, created_at, updated_at)
            VALUES (nextval('users_id_seq'), uuid_in(md5(random()::text || clock_timestamp()::text)::cstring),
                    'SYSTEM_KdnIgwRY0qzedOZ6R2X6T9GtNDz', true, 'INITIATED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Role with ID 5 because: INSERT INTO public.roles(id, name) VALUES (5, 'INNER_SYSTEM') ON CONFLICT DO NOTHING;
            INSERT INTO public.user_roles (user_id, role_id)
            select id, 5
            FROM public.users
            WHERE username like 'SYSTEM_KdnIgwRY0qzedOZ6R2X6T9GtNDz'
            ON CONFLICT DO NOTHING;
        END IF;
    END
$$;






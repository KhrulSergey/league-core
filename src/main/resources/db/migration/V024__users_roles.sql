-- ROLES --
create table if not exists public.roles
(
    id                    bigint not null
        constraint roles_pkey primary key,
    name                  varchar(255),
    created_by_league_id  UUID
        constraint fk_created_by_league_id references public.users (league_id),
    modified_by_league_id UUID
        constraint fk_modified_by_league_id references public.users (league_id),
    created_at            timestamp default now(),
    updated_at            timestamp
);
create sequence if not exists public.roles_id_seq;

comment on table public.roles is 'Table for all user roles';
comment on column public.roles.id is 'Identifier';
comment on column public.roles.name is 'Name';


-- USERs ROLES --
create table if not exists public.user_roles
(
    user_id bigint not null
        constraint user_roles_user_id_fk references users,
    role_id bigint not null
        constraint user_roles_role_id_fk references roles,
    constraint user_roles_pkey primary key (user_id, role_id)
);
comment on table public.user_roles is 'Table for connection user with their roles';

INSERT INTO public.roles(id, name)
VALUES (1, 'ADMIN')
ON CONFLICT DO NOTHING;
INSERT INTO public.roles(id, name)
VALUES (2, 'MANAGER')
ON CONFLICT DO NOTHING;
INSERT INTO public.roles(id, name)
VALUES (3, 'REGULAR')
ON CONFLICT DO NOTHING;
INSERT INTO public.roles(id, name)
VALUES (4, 'EXTERNAL_SERVICE')
ON CONFLICT DO NOTHING;

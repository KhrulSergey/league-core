create table if not exists public.roulette_matches
(
    id                   bigint not null
        constraint roulette_matches_pk primary key,
    created_at           timestamp default now(),
    updated_at           timestamp,
    finished_at          timestamp,
    finished             boolean,
    random_org_id        varchar(50),
    should_started_after timestamp,
    last_ticket_number   bigint,
    bet_sum              bigint
);

create sequence if not exists public.roulette_matches_id_seq;

create table if not exists public.roulette_match_bets
(
    id                 bigint not null
        constraint roulette_match_bets_pk primary key,
    created_at         timestamp default now(),
    updated_at         timestamp,
    league_id          uuid
        constraint fk_roulette_match_bets_user_league_id references public.users (league_id),
    match_id           bigint
        constraint fk_roulette_match_bets_match_id references public.roulette_matches (id),
    ticket_number_from bigint,
    ticket_number_to   bigint,
    ton_amount         bigint
);

create sequence if not exists public.roulette_match_bets_id_seq;

alter table public.roulette_matches
    add column if not exists
        winner_bet_id bigint
            constraint fk_roulette_matches_winner_bet_id references public.roulette_match_bets (id);


insert into league_finance.accounts(id,
                                    guid,
                                    holder_guid,
                                    name,
                                    amount,
                                    type,
                                    status,
                                    external_address,
                                    external_bank_type,
                                    external_bank_last_update_at,
                                    created_by_league_id,
                                    modified_by_league_id,
                                    created_at,
                                    updated_at)
values (nextval('league_finance.accounts_id_seq'),
        uuid_in(md5(random()::text || clock_timestamp()::text)::cstring),
        null,
        'ROULETTE_BANK',
        0,
        'DEPOSIT',
        'ACTIVE',
        'ROULETTE_BANK',
        'FREETON_LEAGUE',
        null,
        null,
        null,
        now(),
        now())

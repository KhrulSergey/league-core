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
        'MPUBG',
        0,
        'DEPOSIT',
        'NOT_TRACKING',
        'MPUBG',
        'UNKNOWN',
        null,
        null,
        null,
        now(),
        now())
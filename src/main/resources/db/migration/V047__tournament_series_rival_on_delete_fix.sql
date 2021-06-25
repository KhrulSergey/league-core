-- MODIFY TOURNAMENT SERIES RIVAL CONSTRAINTS --
alter table if exists public.tournament_series_rivals
    drop constraint if exists fk_tournament_series_parents_id;
-- MODIFY TOURNAMENT SERIES_PARENTS CONSTRAINTS --
alter table if exists public.tournament_series_parents
    drop constraint if exists tournament_series_parents_current_series_id_fk,
    add constraint tournament_series_parents_parent_series_id_fk
        foreign key (parent_series_id) references public.tournament_series (id) ON DELETE CASCADE,
    drop constraint if exists tournament_series_parents_parent_series_id_fk;

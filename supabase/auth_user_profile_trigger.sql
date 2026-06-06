create or replace function public.handle_new_auth_user()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
declare
    created_user_id integer;
    selected_type_id integer;
    modality_name text;
begin
    insert into public.utilizador (
        auth_user_id,
        nome,
        username,
        email,
        data_nascimento
    )
    values (
        new.id,
        coalesce(nullif(new.raw_user_meta_data ->> 'full_name', ''), new.email),
        coalesce(
            nullif(new.raw_user_meta_data ->> 'username', ''),
            concat(split_part(new.email, '@', 1), '_', left(replace(new.id::text, '-', ''), 8))
        ),
        new.email,
        case
            when (new.raw_user_meta_data ->> 'birth_date') ~ '^\d{4}-\d{2}-\d{2}$'
                then (new.raw_user_meta_data ->> 'birth_date')::date
            else null
        end
    )
    on conflict (auth_user_id) do update
    set
        nome = excluded.nome,
        username = excluded.username,
        email = excluded.email,
        data_nascimento = excluded.data_nascimento
    returning id into created_user_id;

    select id
    into selected_type_id
    from public.tipo_utilizador
    where lower(tipo) = any (
        case lower(coalesce(new.raw_user_meta_data ->> 'account_type', ''))
            when 'player' then array['player', 'jogador']
            when 'organizer' then array['organizer', 'organizador']
            else array[]::text[]
        end
    )
    limit 1;

    if selected_type_id is not null then
        insert into public.utilizador_tipoutilizador (user_id, tipo_utilizador_id)
        values (created_user_id, selected_type_id)
        on conflict do nothing;
    end if;

    for modality_name in
        select jsonb_array_elements_text(coalesce(new.raw_user_meta_data -> 'modalities', '[]'::jsonb))
    loop
        insert into public.utilizador_modalidade (user_id, modalidade_id)
        select created_user_id, modalidade.id
        from public.modalidade
        where lower(modalidade.nome) = lower(modality_name)
        on conflict do nothing;
    end loop;

    return new;
end;
$$;

drop trigger if exists on_auth_user_created on auth.users;

create trigger on_auth_user_created
after insert on auth.users
for each row execute function public.handle_new_auth_user();

insert into public.utilizador (
    auth_user_id,
    nome,
    username,
    email,
    data_nascimento
)
select
    auth.users.id,
    coalesce(nullif(auth.users.raw_user_meta_data ->> 'full_name', ''), auth.users.email),
    coalesce(
        nullif(auth.users.raw_user_meta_data ->> 'username', ''),
        concat(split_part(auth.users.email, '@', 1), '_', left(replace(auth.users.id::text, '-', ''), 8))
    ),
    auth.users.email,
    case
        when (auth.users.raw_user_meta_data ->> 'birth_date') ~ '^\d{4}-\d{2}-\d{2}$'
            then (auth.users.raw_user_meta_data ->> 'birth_date')::date
        else null
    end
from auth.users
where not exists (
    select 1
    from public.utilizador
    where public.utilizador.auth_user_id = auth.users.id
)
on conflict (auth_user_id) do nothing;

select
    trigger_name,
    event_object_schema,
    event_object_table,
    action_timing,
    event_manipulation
from information_schema.triggers
where event_object_schema = 'auth'
    and event_object_table = 'users'
    and trigger_name = 'on_auth_user_created';

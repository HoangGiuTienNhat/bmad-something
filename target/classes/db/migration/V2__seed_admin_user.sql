alter table users
    add column if not exists password_hash varchar(255) not null default '';

insert into users (full_name, username, password_hash, role, status)
values (
    'Default Admin',
    'admin',
    '$2b$12$jdhHTyDCZFhyHBnXFYBJPORc3rXyzFiYYhqd9L7E.P1OzkXsLLLzi',
    'ADMIN',
    'ACTIVE'
)
on conflict (username) do nothing;

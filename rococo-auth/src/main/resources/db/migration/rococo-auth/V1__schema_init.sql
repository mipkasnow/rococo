create table if not exists users
(
    id                      uuid unique not null default gen_random_uuid(),
    username                varchar(50) unique not null,
    password                varchar(255)       not null,
    enabled                 boolean            not null,
    account_non_expired     boolean            not null,
    account_non_locked      boolean            not null,
    credentials_non_expired boolean            not null,
    primary key (id, username)
);

create table if not exists authority
(
    id        uuid unique not null default gen_random_uuid(),
    user_id   uuid        not null,
    authority varchar(255) not null,
    primary key (id),
    constraint fk_authorities_users foreign key (user_id) references users (id),
    constraint chk_authority check (authority in ('read', 'write'))
);

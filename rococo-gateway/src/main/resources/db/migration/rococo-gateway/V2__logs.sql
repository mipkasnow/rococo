create table if not exists logs
(
    id          uuid          not null,
    occurred_at timestamptz   not null,
    ip          varchar(64)   not null,
    username    varchar(255)  not null,
    http_method varchar(16)   not null,
    path        varchar(2048) not null,
    http_status integer       not null,
    success     boolean       not null,
    error       text,
    primary key (id)
);

create index if not exists idx_logs_occurred_at on logs (occurred_at);

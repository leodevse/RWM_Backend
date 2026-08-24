create table users (
    id uuid primary key,
    login_identifier varchar(150) not null unique,
    password_hash varchar(255) not null,
    full_name varchar(255),
    role varchar(32) not null,
    account_status varchar(32) not null,
    last_login_at timestamp null
);

create table login_audit_logs (
    id uuid primary key,
    user_id uuid null,
    login_identifier varchar(150) not null,
    outcome varchar(32) not null,
    failure_reason varchar(64) null,
    occurred_at timestamp not null,
    source_ip varchar(64) null,
    constraint fk_login_audit_user foreign key (user_id) references users (id)
);

create index idx_users_login_identifier on users (login_identifier);
create index idx_login_audit_login_identifier on login_audit_logs (login_identifier);

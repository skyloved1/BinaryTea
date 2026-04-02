/*drop table t_menu if exists;
create table t_menu (
                        id bigint auto_increment, name varchar(128),
                        size varchar(16), price bigint,
                        create_time timestamp, update_time timestamp, primary key (id)
);*/
create table if not exists t_demo
(
    id          bigint auto_increment,
    name        varchar(128),
    create_time timestamp,
    update_time timestamp,
    primary key (id)
);


drop table if exists users;
drop table if exists authorities;
create table users
(
    username varchar(50)  not null primary key,
    password varchar(500) not null,
    enabled  boolean      not null
);
create table authorities
(
    username  varchar(50) not null,
    authority varchar(50) not null
);
create unique index ix_auth_username on authorities (username, authority);

create table persistent_logins
(
    username  varchar(64) not null,
    series    varchar(64) primary key,
    token     varchar(64) not null,
    last_used timestamp   not null
);


insert into users (username, password, enabled)
values ('LiLei', '{bcrypt}$2a$10$iAty2GrJu9WfpksIen6qX.vczLmXlp.1q1OHBxWEX8BIldtwxHl3u', true);
insert into authorities (username, authority)
values ('LiLei', 'READ_MENU');
insert into authorities (username, authority)
values ('LiLei', 'READ_ORDER');
insert into authorities (username, authority)
values ('LiLei', 'WRITE_ORDER');
insert into users (username, password, enabled)
values ('ZhangSan',
        '{bcrypt}$2a$10$iAty2GrJu9WfpksIen6qX.vczLmXlp.1q1OHBxWEX8BIldtwxHl3u', true);
insert into authorities (username, authority)
values ('ZhangSan', 'READ_MENU');
insert into authorities (username, authority)
values ('ZhangSan', 'READ_ORDER');

insert into authorities (username, authority)
values ('LiLei', 'ROLE_TEA_MAKER');
insert into authorities (username, authority)
values ('ZhangSan', 'ROLE_USER');
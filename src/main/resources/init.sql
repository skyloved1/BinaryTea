/*drop table t_menu if exists;
create table t_menu (
                        id bigint auto_increment, name varchar(128),
                        size varchar(16), price bigint,
                        create_time timestamp, update_time timestamp, primary key (id)
);*/
create  table if not exists  t_demo(
                                       id bigint auto_increment,
                                       name varchar(128),
                                       create_time timestamp,
                                       update_time timestamp,
                                       primary key (id)
);


drop table if exists users;
drop table if exists authorities;
create table users(
                      username varchar(50) not null primary key, password varchar(500) not null,
                      enabled boolean not null
);
create table authorities (
                             username varchar(50) not null, authority varchar(50) not null
);
create unique index ix_auth_username on authorities (username, authority);
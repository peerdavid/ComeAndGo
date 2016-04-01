# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table user (
  id                            integer not null,
  active                        boolean,
  firstname                     varchar(255),
  lastname                      varchar(255),
  last_login                    timestamp,
  constraint pk_user primary key (id)
);
create sequence user_seq;

create table user_login (
  user_id                       integer not null,
  username                      varchar(255),
  password                      varchar(255),
  security_role                 varchar(255),
  constraint pk_user_login primary key (user_id)
);
create sequence user_login_seq;


# --- !Downs

drop table if exists user;
drop sequence if exists user_seq;

drop table if exists user_login;
drop sequence if exists user_login_seq;


# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table user (
  _id                           integer not null,
  _user_name                    varchar(255),
  _password                     varchar(255),
  _role                         varchar(255),
  _active                       boolean,
  _first_name                   varchar(255),
  _last_name                    varchar(255),
  _email                        varchar(255),
  constraint pk_user primary key (_id)
);
create sequence user_seq;


# --- !Downs

drop table if exists user;
drop sequence if exists user_seq;


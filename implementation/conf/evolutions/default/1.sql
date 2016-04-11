# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table break (
  id                            integer auto_increment not null,
  time_track_id                 integer not null,
  start                         datetime,
  end                           datetime,
  constraint pk_break primary key (id)
);

create table time_track (
  id                            integer auto_increment not null,
  _user_id                      integer not null,
  start                         datetime,
  end                           datetime,
  constraint pk_time_track primary key (id)
);

create table user (
  id                            integer not null,
  user_name_boss                varchar(255),
  username                      varchar(255),
  password                      varchar(255),
  role                          varchar(255),
  active                        boolean,
  firstname                     varchar(255),
  lastname                      varchar(255),
  email                         varchar(255),
  user_name_boss                varchar(255),
  constraint pk_user primary key (id)
);
create sequence user_seq;

alter table break add constraint fk_break_time_track_id foreign key (time_track_id) references time_track (id) on delete restrict on update restrict;
create index ix_break_time_track_id on break (time_track_id);

alter table time_track add constraint fk_time_track__user_id foreign key (_user_id) references user (id) on delete restrict on update restrict;
create index ix_time_track__user_id on time_track (_user_id);


# --- !Downs

alter table break drop constraint if exists fk_break_time_track_id;
drop index if exists ix_break_time_track_id;

alter table time_track drop constraint if exists fk_time_track__user_id;
drop index if exists ix_time_track__user_id;

drop table if exists break;

drop table if exists time_track;

drop table if exists user;
drop sequence if exists user_seq;


# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table break (
  id                            integer auto_increment not null,
  time_track_id                 integer not null,
  start                         time,
  end                           time,
  constraint pk_break primary key (id)
);

create table notification (
  id                            integer auto_increment not null,
  type                          integer,
  message                       varchar(150),
  _sender_id                    integer not null,
  _receiver_id                  integer not null,
  read                          boolean,
  created                       datetime,
  reference_id                  integer,
  constraint ck_notification_type check (type in (0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16)),
  constraint pk_notification primary key (id)
);

create table time_off (
  id                            integer auto_increment not null,
  _user_id                      integer not null,
  start                         time,
  end                           time,
  type                          integer,
  state                         integer,
  comment                       varchar(255),
  constraint ck_time_off_type check (type in (0,1,2,3)),
  constraint ck_time_off_state check (state in (0,1,2,3)),
  constraint pk_time_off primary key (id)
);

create table time_track (
  id                            integer auto_increment not null,
  _user_id                      integer not null,
  start                         datetime,
  end                           datetime,
  constraint pk_time_track primary key (id)
);

create table user (
  id                            integer auto_increment not null,
  username                      varchar(255),
  password                      varchar(255),
  role                          varchar(255),
  active                        boolean,
  firstname                     varchar(255),
  lastname                      varchar(255),
  email                         varchar(255),
  user_name_boss                varchar(255),
  constraint uq_user_username unique (username),
  constraint pk_user primary key (id)
);

alter table break add constraint fk_break_time_track_id foreign key (time_track_id) references time_track (id) on delete restrict on update restrict;
create index ix_break_time_track_id on break (time_track_id);

alter table notification add constraint fk_notification__sender_id foreign key (_sender_id) references user (id) on delete restrict on update restrict;
create index ix_notification__sender_id on notification (_sender_id);

alter table notification add constraint fk_notification__receiver_id foreign key (_receiver_id) references user (id) on delete restrict on update restrict;
create index ix_notification__receiver_id on notification (_receiver_id);

alter table time_off add constraint fk_time_off__user_id foreign key (_user_id) references user (id) on delete restrict on update restrict;
create index ix_time_off__user_id on time_off (_user_id);

alter table time_track add constraint fk_time_track__user_id foreign key (_user_id) references user (id) on delete restrict on update restrict;
create index ix_time_track__user_id on time_track (_user_id);


# --- !Downs

alter table break drop constraint if exists fk_break_time_track_id;
drop index if exists ix_break_time_track_id;

alter table notification drop constraint if exists fk_notification__sender_id;
drop index if exists ix_notification__sender_id;

alter table notification drop constraint if exists fk_notification__receiver_id;
drop index if exists ix_notification__receiver_id;

alter table time_off drop constraint if exists fk_time_off__user_id;
drop index if exists ix_time_off__user_id;

alter table time_track drop constraint if exists fk_time_track__user_id;
drop index if exists ix_time_track__user_id;

drop table if exists break;

drop table if exists notification;

drop table if exists time_off;

drop table if exists time_track;

drop table if exists user;


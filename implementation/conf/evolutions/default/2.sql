# --- !Ups

insert into user values (1, 'sebastian', '$2a$06$F9jjMmhp7eLERO4fosg3N.I7PDSv5pRKElhYKQJ4AfMx6s6e2vlku', 'ROLE_ADMIN', true, 'Sebastian', 'Waldhart', 'admin@comego.at', 1, 8.00, 25, '2016-05-01');

insert into notification VALUES
(1, 0, 'Welcome to Come & Go.', 1, 1, false, '2016-04-23 08:10:09', 0),


# --- !Downs
delete from notification;
delete from user;


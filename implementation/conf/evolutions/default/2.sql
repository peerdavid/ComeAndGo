# --- !Ups
insert into user (id, user_name, password, role, active, first_name, last_name) values (1, 'admin', 'admin', 'admin', true, 'admin', 'admin');


# --- !Downs
delete * from user where username = 'admin';

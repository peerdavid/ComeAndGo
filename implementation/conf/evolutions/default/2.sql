# --- !Ups
insert into user (_id, _user_name, _password, _role, _active, _first_name, _last_name, _email) values (1, 'admin', 'admin', 'ROLE_ADMIN', true, 'admin', 'admin', 'admin@comego.at');


# --- !Downs
delete * from user where username = 'admin';


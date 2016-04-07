# --- !Ups
insert into user (_id, username, _password, _role, _active, _first_name, _last_name, _email)
values (1, 'admin', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_ADMIN', true, 'admin', 'admin', 'admin@comego.at');


# --- !Downs
delete * from user where username = 'admin';


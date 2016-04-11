# --- !Ups
insert into user (id, username, password, role, active, firstname, lastname, email)
values (0, 'admin', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_ADMIN', true, 'admin', 'admin', 'admin@comego.at');


# --- !Downs
delete * from user where username = 'admin';


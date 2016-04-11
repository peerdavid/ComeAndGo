# --- !Ups
insert into user (id, username, password, role, active, firstname, lastname, email, user_name_boss)
values (0, 'admin', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_ADMIN', true, 'admin', 'admin', 'admin@comego.at', 'testboss');


# --- !Downs
delete * from user where username = 'admin';


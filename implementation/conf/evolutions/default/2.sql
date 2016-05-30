# --- !Ups

insert into user values (1, 'sebastian', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_ADMIN', true, 'Sebastian', 'Waldhart', 'admin@comego.at', 1, 5.00, 25, '2016-05-01');

insert into notification VALUES
(1, 0, 'Welcome to Come & Go.', 1, 1, false, '2016-04-23 08:10:09', 0),


# --- !Downs
delete from notification;
delete from user;


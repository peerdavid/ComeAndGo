# --- !Ups
insert into user values (0, 'admin', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_ADMIN', true, 'admin', 'admin', 'admin@comego.at', 'admin'),
(1, 'stefan', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_ADMIN', true, 'Stefan', 'Haberl', 'stefan@comego.at', 'martin'),
(2, 'leo', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_ADMIN', true, 'Leo', 'Haas', 'leo@comego.at', 'martin'),
(3, 'david', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_ADMIN', true, 'David', 'Peer', 'david@comego.at', 'martin'),
(4, 'martin', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_BOSS', true, 'Martin', 'Brunner', 'martin@comego.at', 'martin');


insert into time_track values
(0, 0, '2016-05-01 08:10:09', '2016-05-01 16:34:09'),
(1, 0, '2016-05-02 09:10:09', '2016-05-02 17:30:09'),
(2, 0, '2016-05-03 07:45:09', '2016-05-03 15:57:33'),
(3, 1, '2016-05-01 08:10:09', '2016-05-01 16:34:09'),
(4, 1, '2016-05-02 09:10:09', '2016-05-02 17:30:09'),
(5, 1, '2016-05-03 07:45:09', '2016-05-03 15:57:33'),
(6, 2, '2016-05-01 08:10:09', '2016-05-01 16:34:09'),
(7, 2, '2016-05-02 09:10:09', '2016-05-02 17:30:09'),
(8, 2, '2016-05-03 07:45:09', '2016-05-03 15:57:33'),
(9, 3, '2016-05-01 08:10:09', '2016-05-01 16:34:09'),
(10, 3, '2016-05-02 09:10:09', '2016-05-02 17:30:09'),
(11, 3, '2016-05-03 07:45:09', '2016-05-03 15:57:33');

insert into break VALUES
(0, 0, '11:29:09', '12:08:09'),
(1, 1, '12:44:09', '13:15:09'),
(2, 2, '11:15:09', '12:57:09'),
(3, 3, '11:29:09', '12:08:09'),
(4, 4, '12:44:09', '13:15:09'),
(5, 5, '11:15:09', '12:57:09'),
(6, 6, '11:29:09', '12:08:09'),
(7, 7, '12:44:09', '13:15:09'),
(8, 8, '11:15:09', '12:57:09'),
(9, 9, '11:29:09', '12:08:09'),
(10, 10, '12:44:09', '13:15:09'),
(11, 11, '11:15:09', '12:57:09');

insert into notification VALUES
(0, 0, 'Hallo Stefan :-)', 0, 1, 0, 0, 1, '2016-09-17', '2016-10-08', '2016-01-01 00:00:00'),
(1, 1, 'Hallo Leo :-)', 0, 2, 0, 0, 1, '2016-09-17', '2016-10-08', '2016-01-01 00:00:00'),
(2, 2, 'Hallo David :-)', 0, 3, 0, 0, 1, '2016-09-17', '2016-10-08', '2016-01-01 00:00:00'),
(3, 3, 'Hallo Admin :-)', 1, 0, 0, 0, 1, '2016-09-17', '2016-10-08', '2016-01-01 00:00:00'),
(4, 4, 'Das ist eine NACHRICHT vom Leo', 0, 2, 0, 0, 1, '2016-09-17', '2016-10-08', '2016-01-01 00:00:00'),
(5, 5, 'Nachricht von David', 3, 0, 0, 0, 1, '2016-09-17', '2016-10-08', '2016-01-01 00:00:00');

# --- !Downs
delete * from user;
delete * from time_track;
delete * from break;
delete * from notification;


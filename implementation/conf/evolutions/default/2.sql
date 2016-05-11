# --- !Ups

insert into user values (1, 'david', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_BOSS', true, 'David', 'Peer', 'david@comego.at', 1, 8.30);
insert into user values (2, 'stefan', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_ADMIN', true, 'Stefan', 'Haberl', 'stefan@comego.at', 1, 8.30);
insert into user values (3, 'leo', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_ADMIN', true, 'Leo', 'Haas', 'leo@comego.at', 1, 8.30);
insert into user values (4, 'martin', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_ADMIN', true, 'Martin', 'Brunner', 'martin@comego.at', 1, 5.30);
insert into user values (5, 'admin', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_ADMIN', true, 'admin', 'admin', 'admin@comego.at', 1, 5.00);
insert into user values (6, 'pers', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_PERSONNEL_MANAGER', true, 'pers', 'pers', 'pers@comego.at', 1, 8.30);



insert into time_track values
(1, 4, '2016-05-02 09:10:09', '2016-05-02 17:30:09'),
(2, 4, '2016-05-03 05:45:09', '2016-05-03 15:57:33'),
(3, 1, '2016-05-01 08:10:09', '2016-05-01 16:34:09'),
(4, 1, '2016-05-02 10:10:09', '2016-05-02 17:30:09'),
(5, 1, '2016-05-03 07:45:09', '2016-05-03 15:57:33'),
(6, 2, '2016-05-01 06:12:09', '2016-05-01 16:34:09'),
(7, 2, '2016-05-02 09:10:09', '2016-05-02 17:30:09'),
(8, 2, '2016-05-03 09:35:09', '2016-05-03 15:57:33'),
(9, 3, '2016-05-01 08:18:09', '2016-05-01 16:34:09'),
(10, 3, '2016-05-02 06:10:09', '2016-05-02 17:30:09'),
(11, 3, '2016-05-03 07:45:09', '2016-05-03 15:57:33'),
(12, 5, '2016-05-01 05:15:09', '2016-05-01 16:34:09'),
(13, 5, '2016-05-02 09:12:09', '2016-05-02 17:30:09'),
(14, 5, '2016-05-03 07:45:09', '2016-05-03 15:57:33'),
(15, 4, '2016-05-01 08:10:09', '2016-05-01 16:34:09');

insert into break VALUES
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
(11, 11, '11:15:09', '12:57:09'),
(12, 12, '11:15:09', '12:57:09'),
(13, 13, '11:15:09', '12:57:09'),
(14, 14, '11:15:09', '12:57:09'),
(15, 15, '11:29:09', '12:08:09');

insert into notification VALUES
(1, 1, 'hallo david', 2, 1, 0, '2016-04-23 08:10:09', 0),
(2, 2, 'hallo david', 3, 1, 0, '2016-04-23 08:10:09', 2),
(3, 3, 'hallo david', 4, 1, 0, '2016-04-23 08:10:09', 5),
(4, 6, 'hallo david', 5, 1, 0, '2016-04-23 08:10:09', 23),
(5, 7, 'hallo david', 2, 1, 0, '2016-04-23 08:10:09', 14);

insert into time_off  VALUES
(1, 5, '2016-05-18 08:10:09', '2016-05-25 08:10:09', 0, 0, 'Please give me holiday', NULL),
(2, 5, '2016-05-01 08:10:09', '2016-05-01 08:10:09', 1, 2, 'I dont want to work here', NULL),
(3, 5, '2016-05-25 08:10:09', '2016-05-28 08:10:09', 2, 3, 'Holiday <3', NULL),
(4, 5, '2016-06-23 08:10:09', '2016-06-25 08:10:09', 3, 3, 'Please boss give me holiday', NULL),
(5, 5, '2016-06-01 08:10:09', '2016-06-02 08:10:09', 4, 2, 'Travel to the usa', NULL);

insert into payout VALUES
(1, 1, 10, 0, 0, 'Hallo :-)', NULL),
(2, 1, 2, 1, 1, 'Hallo :-)', 2),
(3, 1, 5, 1, 3, 'Hallo :-)', NULL),
(4, 1, 20, 1, 2, 'Hallo :-)', NULL),
(5, 2, 10, 0, 1, 'Hallo :-)', 1),
(6, 2, 12, 0, 0, 'Hallo :-)', NULL),
(7, 3, 10, 0, 0, 'Hallo :-)', NULL),
(8, 3, 8, 1, 1, 'Hallo :-)', NULL),
(9, 3, 2, 0, 2, 'Hallo :-)', 1),
(10, 4, 3, 0, 3, 'Hallo :-)', 2),
(11, 4, 14, 1, 0, 'Hallo :-)', 6),
(12, 4, 7, 0, 2, 'Hallo :-)', NULL),
(13, 5, 9, 1, 3, 'Hallo :-)', NULL),
(14, 5, 1, 0, 1, 'Hallo :-)', 4),
(15, 6, 2, 0, 2, 'Hallo :-)', NULL);

# --- !Downs
delete from break;
delete from time_track;
delete from notification;
delete from time_off;
delete from user;


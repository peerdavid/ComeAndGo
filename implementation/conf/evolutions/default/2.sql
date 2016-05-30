# --- !Ups

insert into user values (1, 'david', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_BOSS', true, 'David', 'Peer', 'david@comego.at', 1, 8.30, 25, '2016-01-01');
insert into user values (2, 'stefan', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_USER', true, 'Stefan', 'Haberl', 'stefan@comego.at', 1, 8.30, 25, '2016-01-01');
insert into user values (3, 'leo', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_USER', true, 'Leo', 'Haas', 'leo@comego.at', 1, 8.30, 25, '2016-01-01');
insert into user values (4, 'martin', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_USER', true, 'Martin', 'Brunner', 'martin@comego.at', 1, 5.30, 25, '2016-01-01');
insert into user values (5, 'admin', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_ADMIN', true, 'admin', 'admin', 'admin@comego.at', 1, 5.00, 25, '2016-01-01');
insert into user values (6, 'pers', '$2a$10$x5hq8cYHsF2UOCElx7v29.FAleENAZ//oHgii6DdAyarcBe514KtS', 'ROLE_PERSONNEL_MANAGER', true, 'pers', 'pers', 'pers@comego.at', 1, 8.30, 25, '2016-01-01');



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
(15, 4, '2016-05-01 08:10:09', '2016-05-01 16:34:09'),
(16, 4, '2016-05-02 08:10:09', '2016-05-02 16:34:09'),
(17, 4, '2016-05-03 08:10:09', '2016-05-03 16:34:09'),
(18, 4, '2016-05-04 08:10:09', '2016-05-04 16:34:09'),
(19, 4, '2016-05-05 08:10:09', '2016-05-05 16:34:09'),
(20, 4, '2016-05-06 08:10:09', '2016-05-06 16:34:09'),
(21, 4, '2016-05-07 08:10:09', '2016-05-07 16:34:09'),
(22, 4, '2016-05-08 08:10:09', '2016-05-08 16:34:09'),
(23, 4, '2016-05-09 08:10:09', '2016-05-09 16:34:09'),
(24, 4, '2016-05-10 08:10:09', '2016-05-10 16:34:09'),
(25, 4, '2016-05-11 08:10:09', '2016-05-11 16:34:09'),
(26, 4, '2016-05-12 08:10:09', '2016-05-12 16:34:09'),
(27, 4, '2016-05-13 08:10:09', '2016-05-13 16:34:09'),
(28, 4, '2016-05-14 08:10:09', '2016-05-14 16:34:09'),
(29, 4, '2016-05-15 08:10:09', '2016-05-15 16:34:09'),
(30, 4, '2016-05-16 08:10:09', '2016-05-16 16:34:09'),
(31, 4, '2016-05-17 08:10:09', '2016-05-17 16:34:09'),
(32, 4, '2016-05-18 08:10:09', '2016-05-18 16:34:09'),
(33, 4, '2016-05-19 08:10:09', '2016-05-19 16:34:09'),
(34, 4, '2016-05-20 08:10:09', '2016-05-20 16:34:09');

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
(1, 5, '2016-04-18 08:10:09', '2016-04-25 08:10:09', 0, 1, 'Please give me holiday', NULL),
(2, 5, '2016-05-01 08:10:09', '2016-05-01 08:10:09', 1, 2, 'I dont want to work here', NULL),
(3, 5, '2016-05-25 08:10:09', '2016-05-28 08:10:09', 2, 3, 'Holiday <3', NULL),
(4, 5, '2016-06-23 08:10:09', '2016-06-25 08:10:09', 3, 3, 'Please boss give me holiday', NULL),
(5, 5, '2016-06-01 08:10:09', '2016-06-02 08:10:09', 4, 2, 'Travel to the usa', NULL);

insert into payout VALUES
(1, 1, 10, 0, 0, 'Hallo :-)', NULL, '2000-01-01'),
(2, 1, 2, 1, 1, 'Hallo :-)', 2, '2000-01-01'),
(3, 1, 5, 1, 3, 'Hallo :-)', NULL, '2000-01-01'),
(4, 1, 20, 1, 2, 'Hallo :-)', NULL, '2000-01-01'),
(5, 2, 10, 0, 1, 'Hallo :-)', 1, '2000-01-01'),
(6, 2, 12, 0, 0, 'Hallo :-)', NULL, '2000-01-01'),
(7, 3, 10, 0, 0, 'Hallo :-)', NULL, '2000-01-01'),
(8, 3, 8, 1, 1, 'Hallo :-)', NULL, '2000-01-01'),
(9, 3, 2, 0, 2, 'Hallo :-)', 1, '2000-01-01'),
(10, 4, 3, 0, 3, 'Hallo :-)', 2, '2000-01-01'),
(11, 4, 14, 1, 0, 'Hallo :-)', 6, '2000-01-01'),
(12, 4, 7, 0, 2, 'Hallo :-)', NULL, '2000-01-01'),
(13, 5, 9, 1, 3, 'Hallo :-)', NULL, '2000-01-01'),
(14, 5, 1, 0, 1, 'Hallo :-)', 4, '2000-01-01'),
(15, 6, 2, 0, 2, 'Hallo :-)', NULL, '2000-01-01');

# --- !Downs
delete from break;
delete from time_track;
delete from notification;
delete from time_off;
delete from user;


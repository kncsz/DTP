create table users
(
    id       bigint auto_increment
        primary key,
    username varchar(50)  not null,
    password varchar(100) not null,
    email    varchar(100) not null,
    role     varchar(20)  not null,
    constraint username
        unique (username)
);

INSERT INTO micro.users (id, username, password, email, role) VALUES (8, 'testuser', 'Test1234', 'testuser@example.com', 'USER');

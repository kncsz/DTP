create table devices
(
    id           bigint auto_increment
        primary key,
    name         varchar(100) null,
    status       varchar(50)  null,
    last_updated timestamp    null
);

INSERT INTO micro.devices (id, name, status, last_updated) VALUES (1001, 'RTX 4090', '100%满载', '2024-10-15 12:12:12');

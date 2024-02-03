create table if not exists location
(
    id               int auto_increment
    primary key,
    latitude         varchar(255) null,
    longitude        varchar(255) null,
    formatted_name   text         null,
    province         varchar(255) null,
    city             varchar(255) null,
    district         varchar(255) null,
    adcode           varchar(255) null,
    citycode         varchar(255) null,
    street_direction varchar(255) null,
    street_distance  varchar(255) null,
    street_location  varchar(255) null,
    street_name      varchar(255) null,
    street_number    varchar(255) null,
    towncode         varchar(255) null,
    township         varchar(255) null
    );

create table if not exists transaction
(
    id          int auto_increment
    primary key,
    amount      decimal(11, 4) null,
    order_no    varchar(255)   null,
    category_id int            null,
    description text           null,
    location_id int            null,
    datetime    varchar(255)   null,
    count       int            null
    );

create table if not exists transaction_category
(
    id    int auto_increment
    primary key,
    value varchar(255) null,
    icon  varchar(255) null
    );

create table if not exists ledger
(
    id    int auto_increment
    primary key,
    name  varchar(255) null,
    ctime varchar(255) null,
    constraint name
    unique (name)
    );


create table if not exists ledger_transaction
(
    id             int auto_increment
    primary key,
    ledger_id      int null,
    transaction_id int null
);

create table if not exists `user`
(
    id       int auto_increment
    primary key,
    username varchar(255) null,
    password varchar(255) null,
    ctime    varchar(255) null
    );

create table if not exists user_config
(
    id    int auto_increment
    primary key,
    `key` varchar(255) null,
    `value` varchar(255) null
    );

create table if not exists user_user_config
(
    id             int auto_increment
    primary key,
    user_id        int null,
    user_config_id int null
);

create table if not exists user_ledger
(
    id        int auto_increment
    primary key,
    ledger_id int null,
    user_id   int null
);

create table if not exists user_record_type
(
    id      int auto_increment
    primary key,
    type_id int null,
    user_id int null
);

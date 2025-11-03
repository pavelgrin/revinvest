create table Statement
(
    id            integer primary key,
    isoDate       varchar(255) not null,
    date          varchar(255) not null,
    timestamp     integer      not null unique,
    ticker        varchar(255),
    type          varchar(255) not null,
    quantity      real,
    pricePerShare real,
    amount        real         not null,
    currency      varchar(255) not null,
    fxRate        real         not null
);

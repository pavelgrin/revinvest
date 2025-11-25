CREATE TABLE Statement
(
    id            INTEGER      PRIMARY KEY,
    isoDate       VARCHAR(255) NOT NULL,
    date          VARCHAR(255) NOT NULL,
    timestamp     INTEGER      NOT NULL UNIQUE,
    ticker        VARCHAR(255),
    type          VARCHAR(255) NOT NULL,
    quantity      REAL,
    pricePerShare REAL,
    amount        REAL         NOT NULL,
    currency      VARCHAR(255) NOT NULL,
    fxRate        REAL         NOT NULL
);

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Has to set up migration

CREATE TABLE IF NOT EXISTS user_account
(
    id              UUID    PRIMARY KEY    UNIQUE     DEFAULT uuid_generate_v4(),
    email           VARCHAR(100)       UNIQUE NOT NULL CHECK ( user_account.email <> '' ),
    username       VARCHAR(50)        UNIQUE  NOT NULL CHECK ( user_account.username <> '' ),
    hashed_password           VARCHAR(100) UNIQUE NOT NULL CHECK ( user_account.hashed_password <> '' ),
    account_verified         BOOLEAN       NOT NULL DEFAULT FALSE,
    temp_token           VARCHAR(100000) UNIQUE CHECK ( user_account.temp_token <> '' ),
    created_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP
);


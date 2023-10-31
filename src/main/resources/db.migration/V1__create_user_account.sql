CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Has to set up migration

CREATE TABLE IF NOT EXISTS user_account
(
    id              UUID                   UNIQUE     DEFAULT uuid_generate_v4(),
    email           VARCHAR(100)       UNIQUE NOT NULL CHECK ( user_account.email <> '' ),
    username       VARCHAR(60)        UNIQUE  NOT NULL CHECK ( user_account.username <> '' ),
    hashed_password           VARCHAR(100) UNIQUE NOT NULL CHECK ( user_account.hashed_password <> '' ),
    account_verified         BOOLEAN       NOT NULL DEFAULT FALSE,
    temp_token           VARCHAR(1000) UNIQUE CHECK ( user_account.temp_token <> '' ),
    created_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE user_account
ALTER COLUMN temp_token TYPE VARCHAR(100000)
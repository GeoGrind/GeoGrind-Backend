CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Has to set up migration

CREATE TABLE IF NOT EXISTS message
(
    id              UUID                   UNIQUE     DEFAULT uuid_generate_v4(),
    email           VARCHAR(100)       UNIQUE NOT NULL CHECK ( message.email <> '' ),
    created_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP
                                  );


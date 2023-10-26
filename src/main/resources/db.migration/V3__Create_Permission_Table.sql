CREATE TABLE permissions (
    permission_id              UUID                   UNIQUE     DEFAULT uuid_generate_v4(),
    permission_name           VARCHAR(100)       UNIQUE NOT NULL CHECK ( permissions.name <> '' ),
    user_id        UUID,
    created_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES user_account (id)
);


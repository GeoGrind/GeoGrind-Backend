CREATE TYPE permission_name_enum_type AS ENUM (
    'CAN_VERIFY_OTP',
    'CAN_VIEW_PROFILE',
    'CAN_EDIT_PROFILE',
    'CAN_VIEW_MAP',
    'CAN_CREATE_SESSION',
    'CAN_STOP_SESSION',
    'CAN_VIEW_CHAT',
    'CAN_EDIT_CHAT'
);

CREATE TABLE IF NOT EXISTS permissions
(
    permission_id              UUID                   UNIQUE     DEFAULT uuid_generate_v4(),
    permission_name       permission_name_enum_type     NOT NULL,
    fk_user_account_id      UUID,
    created_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE permissions
    ALTER COLUMN permission_name TYPE permission_name_enum_type
        USING permission_name::permission_name_enum_type;


ALTER TABLE permissions
ALTER COLUMN permission_name TYPE VARCHAR(100);

DELETE FROM permissions;

-- Define the ENUM Type for the permissions
CREATE TYPE permission_name_enum as ENUM (
    'CAN_VERIFY_OTP',
    'CAN_VIEW_PROFILE',
    'CAN_EDIT_PROFILE',
    'CAN_VIEW_MAP',
    'CAN_CREATE_SESSION',
    'CAN_STOP_SESSION',
    'CAN_VIEW_CHAT',
    'CAN_EDIT_CHAT'
);

-- Alter the table to change the data type of permission_name to the ENUM type
ALTER TABLE permissions
ALTER COLUMN permission_name
TYPE permission_name_enum
USING permission_name::permission_name_enum;

-- Add a constraint to ensure data integrity
ALTER TABLE permissions
ADD CONSTRAINT permissions_permission_name_check CHECK ( permission_name IS NOT NULL )



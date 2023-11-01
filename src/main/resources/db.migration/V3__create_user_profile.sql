CREATE TABLE IF NOT EXISTS user_profile
(
    profile_id              UUID       UNIQUE   NOT NULL    DEFAULT uuid_generate_v4(),
    username                VARCHAR(60)     NOT NULL    UNIQUE  CHECK ( user_profile.username <> '' ),
    emoji                   VARCHAR(1000)   NULL    DEFAULT 'No emoji set',
    program                 VARCHAR(100)    NULL,
    year_of_graduation      INTEGER         NULL,
    university              VARCHAR(1000)   NOT NULL    DEFAULT 'University of Waterloo',
    created_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP
);
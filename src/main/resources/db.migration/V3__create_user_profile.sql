CREATE TABLE IF NOT EXISTS user_profile
(
    profile_id              UUID        PRIMARY KEY     UNIQUE   NOT NULL    DEFAULT uuid_generate_v4(),
    fk_user_account_id      UUID       UNIQUE   NOT NULL,
    username                VARCHAR(60)     NOT NULL    UNIQUE  CHECK ( user_profile.username <> '' ),
    emoji                   VARCHAR(1000)   NULL    DEFAULT 'No emoji set',
    program                 VARCHAR(100)    NULL,
    year_of_graduation      INTEGER         NULL,
    university              VARCHAR(100)   NOT NULL    DEFAULT 'UNIVERSITY_OF_WATERLOO',
    created_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (fk_user_account_id) REFERENCES user_account(id)
);

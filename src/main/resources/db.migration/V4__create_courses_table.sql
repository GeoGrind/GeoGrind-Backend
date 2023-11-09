CREATE TABLE IF NOT EXISTS courses
(
    course_id               UUID    PRIMARY KEY     UNIQUE  NOT NULL    DEFAULT  uuid_generate_v4(),
    fk_user_profile_id      UUID    UNIQUE          NOT NULL,
    course_code             VARCHAR(10)     NOT NULL,
    course_name             VARCHAR(50)     NOT NULL,
    created_at              TIMESTAMP WITH TIME ZONE    DEFAULT     CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP WITH TIME ZONE    DEFAULT     CURRENT_TIMESTAMP,

    FOREIGN KEY (fk_user_profile_id) REFERENCES user_profile(profile_id)
);
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Has to set up migration
CREATE TABLE IF NOT EXISTS message (
   id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
   author UUID NOT NULL REFERENCES user_account(id),
   created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
   text TEXT NOT NULL,
   type TEXT NOT NULL,
   updated_at TIMESTAMP WITH TIME ZONE NULL
);


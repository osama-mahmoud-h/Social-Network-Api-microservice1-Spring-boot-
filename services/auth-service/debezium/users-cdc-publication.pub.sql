ALTER TABLE users REPLICA IDENTITY FULL;

DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_publication WHERE pubname = 'auth_users_pub') THEN
    EXECUTE 'CREATE PUBLICATION auth_users_pub FOR TABLE users WHERE (email_verified = true)';
    RAISE NOTICE 'Created publication auth_users_pub';
  ELSE
    RAISE NOTICE 'Publication auth_users_pub already exists';
  END IF;
END $$;
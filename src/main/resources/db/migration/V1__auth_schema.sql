-- spring-api owns the `auth` schema: identities, credentials and tokens.
-- express-api reads auth.users (read-only) and references it with foreign keys,
-- so column names here are part of the cross-service contract.

CREATE SCHEMA IF NOT EXISTS auth;

CREATE TABLE auth.users (
  id             uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  -- stored lower-cased (enforced), so plain equality is a case-insensitive match
  email          varchar(254) NOT NULL UNIQUE CHECK (email = lower(email)),
  password_hash  varchar(100) NOT NULL,              -- BCrypt
  full_name      varchar(150) NOT NULL,
  phone          varchar(20),
  role           varchar(20) NOT NULL
                 CHECK (role IN ('PATIENT', 'DOCTOR', 'RECEPTIONIST', 'ADMIN')),
  enabled        boolean NOT NULL DEFAULT true,
  email_verified boolean NOT NULL DEFAULT false,
  last_login_at  timestamptz,
  created_at     timestamptz NOT NULL DEFAULT now(),
  updated_at     timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX users_role_idx ON auth.users (role);

-- Opaque refresh tokens; only a SHA-256 hash is stored. Tokens issued by rotating one
-- another share a family_id, so reuse of a revoked token can revoke the whole family.
CREATE TABLE auth.refresh_tokens (
  id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id     uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  token_hash  varchar(64) NOT NULL UNIQUE,
  family_id   uuid NOT NULL,
  expires_at  timestamptz NOT NULL,
  revoked_at  timestamptz,
  replaced_by uuid REFERENCES auth.refresh_tokens(id),
  user_agent  varchar(255),
  ip_address  varchar(45),
  created_at  timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX refresh_tokens_user_idx   ON auth.refresh_tokens (user_id);
CREATE INDEX refresh_tokens_family_idx ON auth.refresh_tokens (family_id);

CREATE TABLE auth.password_reset_tokens (
  id         uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id    uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  token_hash varchar(64) NOT NULL UNIQUE,
  expires_at timestamptz NOT NULL,
  used_at    timestamptz,
  created_at timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX password_reset_tokens_user_idx ON auth.password_reset_tokens (user_id);

CREATE TABLE auth.email_verification_tokens (
  id         uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id    uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  token_hash varchar(64) NOT NULL UNIQUE,
  expires_at timestamptz NOT NULL,
  used_at    timestamptz,
  created_at timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX email_verification_tokens_user_idx ON auth.email_verification_tokens (user_id);

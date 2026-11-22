-- 005 - One-time codes (stored as HMAC) and accepted legal documents.
-- 'age' records the confirmation of being 18 or older at registration.
-- Reversal: drop table consents; drop table verification_codes;
create table verification_codes (
  id uuid primary key,
  email citext not null check (char_length(email) <= 254),
  purpose code_purpose not null,
  code_hash text not null,
  attempts smallint not null default 0 check (attempts >= 0),
  expires_at timestamptz not null,
  consumed_at timestamptz,
  created_at timestamptz not null default now(),
  ip_hash text
);

create index verification_codes_lookup_idx on verification_codes (email, purpose, created_at desc);

create table consents (
  id uuid primary key,
  user_id uuid not null references users (id) on delete cascade,
  document text not null check (document in ('privacy', 'terms', 'age')),
  version text not null check (char_length(version) between 1 and 20),
  accepted_at timestamptz not null default now()
);

create index consents_user_idx on consents (user_id);

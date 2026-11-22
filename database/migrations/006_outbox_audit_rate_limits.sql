-- 006 - Mail queue, security audit log and rate limit events.
-- rate_limit_events is shared state for limits: serverless instances have no common memory.
-- Reversal: drop table rate_limit_events; drop table audit_log; drop table email_outbox;
create table email_outbox (
  id uuid primary key,
  user_id uuid references users (id) on delete cascade,
  to_email citext not null check (char_length(to_email) <= 254),
  template text not null check (char_length(template) <= 40),
  locale text not null check (locale ~ '^[a-z]{2}(-[A-Z]{2})?$'),
  payload jsonb not null default '{}'::jsonb,
  priority smallint not null check (priority between 1 and 4),
  status email_status not null default 'pending',
  attempts smallint not null default 0 check (attempts >= 0),
  last_error text check (char_length(last_error) <= 500),
  created_at timestamptz not null default now(),
  sent_at timestamptz
);

create index email_outbox_queue_idx on email_outbox (status, priority, created_at);

create table audit_log (
  id bigint generated always as identity primary key,
  user_id uuid references users (id) on delete cascade,
  event text not null check (char_length(event) <= 40),
  device_id uuid,
  ip_hash text,
  created_at timestamptz not null default now()
);

create index audit_log_user_idx on audit_log (user_id, created_at desc);

create table rate_limit_events (
  id bigint generated always as identity primary key,
  action text not null check (char_length(action) <= 40),
  key_hash text not null,
  created_at timestamptz not null default now()
);

create index rate_limit_events_lookup_idx on rate_limit_events (action, key_hash, created_at desc);

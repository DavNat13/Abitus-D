-- 002 - Enumerations used by identity and mail tables (2026-11-22).
-- Reversal: drop type email_status; drop type code_purpose; (only after dropping dependent tables).
create type code_purpose as enum ('verify_email', 'reset_password');
create type email_status as enum ('pending', 'sent', 'failed');

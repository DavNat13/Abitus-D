-- 001 - Extensions required by the schema (2026-11-22).
-- citext: case-insensitive email column (08-DATABASE.md).
-- Reversal: "drop extension if exists citext;" only while no table uses it.
create extension if not exists citext;

-- 011 - Hash of the seed record of each catalog exercise. The seed loader compares it to
-- update only what changed and to raise catalog_version only when needed.
-- Reversal: alter table exercises drop column content_hash;
alter table exercises add column content_hash text;

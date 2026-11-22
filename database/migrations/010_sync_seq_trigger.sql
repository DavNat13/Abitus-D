-- 010 - Every update of a synced row takes a new server_seq, so incremental pulls never
-- miss a change. Reusable by every synced table.
-- Reversal: drop trigger exercises_bump_server_seq on exercises; drop function bump_server_seq();
create function bump_server_seq() returns trigger
language plpgsql as $$
begin
  new.server_seq := nextval('sync_seq');
  return new;
end;
$$;

create trigger exercises_bump_server_seq
  before update on exercises
  for each row execute function bump_server_seq();

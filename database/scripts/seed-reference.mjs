// Upserts disciplines, muscle groups and equipment with their translations.
import { readJsonl } from './seed-files.mjs';

export async function loadReference(client) {
  const disciplines = await readJsonl('reference/disciplines.jsonl');
  for (const d of disciplines) {
    await client.query(
      `insert into disciplines (id, slug, sort_order, icon_key) values ($1, $2, $3, $4)
       on conflict (id) do update set slug = $2, sort_order = $3, icon_key = $4`,
      [d.id, d.slug, d.sort, d.icon],
    );
    for (const locale of ['es', 'en']) {
      await client.query(
        `insert into discipline_translations (discipline_id, locale, name, description) values ($1, $2, $3, $4)
         on conflict (discipline_id, locale) do update set name = $3, description = $4`,
        [d.id, locale, d[locale].name, d[locale].description],
      );
    }
  }
  const muscles = await readJsonl('reference/muscle_groups.jsonl');
  for (const m of muscles) {
    await client.query(
      `insert into muscle_groups (id, slug, body_region, view, mesh_key, svg_key, sort_order)
       values ($1, $2, $3, $4, $5, $6, $7)
       on conflict (id) do update set slug = $2, body_region = $3, view = $4, mesh_key = $5, svg_key = $6, sort_order = $7`,
      [m.id, m.slug, m.region, m.view, `mesh_${m.slug}`, `svg_${m.slug}`, m.sort],
    );
    await upsertName(client, 'muscle_group_translations', 'muscle_group_id', m);
  }
  const equipment = await readJsonl('reference/equipment.jsonl');
  for (const q of equipment) {
    await client.query(
      'insert into equipment (id, slug) values ($1, $2) on conflict (id) do update set slug = $2',
      [q.id, q.slug],
    );
    await upsertName(client, 'equipment_translations', 'equipment_id', q);
  }
  return {
    disciplines: new Map(disciplines.map((d) => [d.slug, d.id])),
    muscles: new Map(muscles.map((m) => [m.slug, m.id])),
    equipment: new Map(equipment.map((q) => [q.slug, q.id])),
  };
}

// Table and column names are constants of this file, never user input.
async function upsertName(client, table, column, row) {
  for (const locale of ['es', 'en']) {
    await client.query(
      `insert into ${table} (${column}, locale, name) values ($1, $2, $3)
       on conflict (${column}, locale) do update set name = $3`,
      [row.id, locale, row[locale]],
    );
  }
}

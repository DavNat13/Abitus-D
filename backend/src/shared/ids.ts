// Time-ordered UUID v7 identifiers for server-created rows.
import { v7 } from 'uuid';

export function newId(): string {
  return v7();
}

# database

Capa de base de datos de Abitus-D (PostgreSQL 17). Independiente del backend.

- `migrations/`: SQL numerado `NNN_descripcion.sql`, inmutable una vez aplicado.
- `seeds/`: catálogo por disciplina (desde la Fase 2).
- `queries/`: consultas críticas documentadas.
- `scripts/migrate.mjs`: aplica las migraciones pendientes en orden, cada una en una transacción, y aborta si una ya aplicada cambió (checksum SHA-256).

Uso en PowerShell desde `C:\Abitus-D\database` (lee `DATABASE_URL` de `..\.env`):

```powershell
npm install
npm run migrate:status
npm run migrate
```

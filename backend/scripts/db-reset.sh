#!/usr/bin/env bash
# backend/scripts/db-reset.sh
#
# Wipe and rebuild the database from scratch, then reseed.
#
#   usage: bash scripts/db-reset.sh
#
# Run from anywhere: the script cds into backend/ itself.
#
# Destructive: deletes ALL data in the configured database (DB_DATABASE
# in .env). Refuses to run if the database name contains "production".

set -euo pipefail

cd "$(dirname "$0")/.."

if [[ ! -f .env ]]; then
    echo "error: no .env found in backend/ — copy .env.example first" >&2
    exit 1
fi

# Read DB_DATABASE from .env (no full env load needed)
DB_NAME="$(grep -E '^DB_DATABASE=' .env | head -1 | cut -d= -f2- | tr -d '"' || true)"
if [[ -z "$DB_NAME" ]]; then
    echo "error: DB_DATABASE not set in .env" >&2
    exit 1
fi
if [[ "$DB_NAME" == *production* ]]; then
    echo "error: refusing to reset a database that looks like production: $DB_NAME" >&2
    exit 1
fi

echo "==> Migrating fresh: database '$DB_NAME'"
php artisan migrate:fresh --force

echo "==> Seeding default data (nurses, patients, caring types, carings)"
php artisan db:seed --force

echo "==> Done."
echo "    Log in with the seeded admin account (see database/seeders/NurseSeeder.php)."
echo "    Note: re-seeding carings can hit the UNIQUE constraint on their"
echo "    'time' column if the seed data was not cleared — this script's"
echo "    migrate:fresh step always avoids that."

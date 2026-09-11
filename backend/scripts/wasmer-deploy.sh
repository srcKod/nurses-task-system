#!/usr/bin/env bash
# backend/scripts/wasmer-deploy.sh
#
# Deploy the enurse Laravel backend to Wasmer Edge.
#
# Prerequisites:
#   1. wasmer CLI installed: https://docs.wasmer.io/edge/getting-started/install
#   2. Logged in:           wasmer login
#   3. Secret DB vars set:  wasmer app secrets create DB_HOST <host>
#                            wasmer app secrets create DB_USERNAME <user>
#                            wasmer app secrets create DB_PASSWORD <pass>
#                            wasmer app secrets create DB_DATABASE enurse
#
# Usage:
#   bash scripts/wasmer-deploy.sh         # deploy to production
#   bash scripts/wasmer-deploy.sh --dry   # show what would be deployed

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$(dirname "$SCRIPT_DIR")"

cd "$BACKEND_DIR"

# Verify prereqs
if ! command -v wasmer &>/dev/null; then
    echo "error: wasmer CLI not found — install from https://docs.wasmer.io/edge/getting-started/install" >&2
    exit 1
fi

# Verify .env exists
if [[ ! -f .env ]]; then
    echo "error: no .env found — copy .env.example first" >&2
    exit 1
fi

# Check DB vars are set (not empty placeholders)
for var in DB_HOST DB_USERNAME DB_PASSWORD DB_DATABASE; do
    val="$(grep -E "^${var}=" .env | head -1 | cut -d= -f2-)" || true
    if [[ -z "$val" || "$val" == *"<"* ]]; then
        echo "warning: ${var} in .env looks like a placeholder — set it before deploying" >&2
    fi
done

DRY=""
if [[ "${1:-}" == "--dry" ]]; then
    DRY="--dry-run"
    echo "=== DRY RUN — no deployment will happen ==="
fi

echo "==> Generating API docs"
php artisan l5-swagger:generate

echo "==> Running migrations"
php artisan migrate --force

echo "==> Seeding database"
php artisan db:seed --force

echo "==> Deploying to Wasmer Edge"
wasmer deploy . \
    --name nurses-task-system \
    --namespace srcKod \
    ${DRY:+$DRY}

echo "==> Done. App URL: https://nurses-task-system.wasmer.app"

#!/usr/bin/env bash
# scripts/api-sweep.sh
#
# Endpoint sweep for the enurse API. Hits every /api/* route once with a
# representative payload and captures the HTTP status, response body, and
# a one-line classification (PASS / 4xx / 5xx / WEIRD).
#
# Run from the backend/ directory:
#   bash scripts/api-sweep.sh
#
# Output:
#   - Full per-test log to scripts/api-sweep.log
#   - Human-readable summary table to stdout
#
# Requires: curl, php (used for JSON parsing — no jq dependency).

set -u
HOST="http://127.0.0.1:8000"
LOG="scripts/api-sweep.log"
STAMP="$(date +%Y%m%d-%H%M%S)"
BODY_FILE="scripts/.last-body.json"
: > "$LOG"
# Don't pre-clear BODY_FILE — call() will write to it

# json_path <path>  — extract a value from the most recent body
# Reads from $BODY_FILE (set by call() after each request)
json_path() {
    local path="$1"
    [[ ! -s "$BODY_FILE" ]] && return
    cat > /tmp/jp.php <<'PHP'
<?php
$d = json_decode(file_get_contents($argv[1]), true);
$keys = explode('.', $argv[2]);
$v = $d;
foreach ($keys as $k) {
    if (is_array($v) && array_key_exists($k, $v)) {
        $v = $v[$k];
    } else {
        $v = null;
        break;
    }
}
echo is_scalar($v) ? $v : (is_null($v) ? '' : json_encode($v));
PHP
    php /tmp/jp.php "$BODY_FILE" "$path" 2>/dev/null
}

hr() { printf '\n%s\n' "============================================================"; }

# call <test_id> <method> <path> [<json_body>]
call() {
    local id="$1" method="$2" path="$3" body="${4:-}"
    local url="${HOST}${path}"
    local args=(-s -o "$BODY_FILE" -w "%{http_code}" -X "$method")
    if [[ -n "$body" ]]; then
        args+=(-H "Content-Type: application/json" -H "Accept: application/json" -d "$body")
    fi
    if [[ -n "${TOKEN:-}" ]]; then
        args+=(-H "Authorization: Bearer $TOKEN")
    fi
    STATUS=$(curl "${args[@]}" "$url" 2>/dev/null) || STATUS="curl-failed"
    BODY=$(cat "$BODY_FILE" 2>/dev/null || echo "")
    CLASS="PASS"
    case "$STATUS" in
        2*) CLASS="PASS" ;;
        400|401|403|404|405|409|422|429) CLASS="4xx" ;;
        5*) CLASS="5xx" ;;
        curl-failed) CLASS="FAIL" ;;
        *) CLASS="WEIRD" ;;
    esac
    {
        echo "----- TEST $id : $method $path -----"
        echo "STATUS: $STATUS  CLASS: $CLASS"
        echo "BODY: $BODY"
        echo
    } >> "$LOG"
    printf "%-8s %-4s %-7s %-50s\n" "$id" "$STATUS" "$CLASS" "$method $path"
}

# Phase 1: AUTH FLOW
hr; echo "=== PHASE 1: AUTH FLOW (no token) ==="
call A1 POST /api/auth/login '{"email":"admin@example.com","password":"123456789"}'
TOKEN=$(json_path 'access_token')
[[ -z "$TOKEN" ]] && TOKEN=$(json_path 'data.access_token')
[[ -z "$TOKEN" ]] && TOKEN=$(json_path 'token')
if [[ -z "$TOKEN" || "$TOKEN" == "null" ]]; then
    echo "LOGIN FAILED — cannot continue with protected routes"
    echo "Last login body:"; cat /tmp/sweep-body
    exit 1
fi
echo "(got token: ${TOKEN:0:30}...)"

call A2 POST /api/auth/login '{"email":"admin@example.com","password":"WRONG"}'
call A3 POST /api/auth/register "{\"name\":\"sweep_${STAMP}\",\"email\":\"sweep_${STAMP}@test.com\",\"password\":\"123456789\"}"
call A4 GET  /api/auth/nurseProfile
call A5 POST /api/auth/refresh
call A6 POST /api/auth/logout

# Re-login in case logout invalidated the token
hr; echo "=== Re-login (in case logout invalidated token) ==="
call A1b POST /api/auth/login '{"email":"admin@example.com","password":"123456789"}'
NEW_TOKEN=$(json_path 'access_token')
[[ -z "$NEW_TOKEN" ]] && NEW_TOKEN=$(json_path 'data.access_token')
[[ -n "$NEW_TOKEN" && "$NEW_TOKEN" != "null" ]] && TOKEN="$NEW_TOKEN"

# Phase 2: NURSES
hr; echo "=== PHASE 2: NURSES ==="
call N1 GET    /api/nurses
call N2 POST   /api/nurses "{\"name\":\"sweep_n_${STAMP}\",\"email\":\"sweep_n_${STAMP}@test.com\",\"password\":\"123456789\"}"
NEW_NURSE_ID=$(json_path 'data.id'); [[ -z "$NEW_NURSE_ID" ]] && NEW_NURSE_ID=$(json_path 'id')
call N3 POST   /api/nurses '{}'
call N4 GET    /api/nurses/1
call N5 PATCH  /api/nurses/1 '{"name":"updated-by-sweep"}'
call N6 DELETE "/api/nurses/${NEW_NURSE_ID:-99999}"

# Phase 3: PATIENTS
hr; echo "=== PHASE 3: PATIENTS ==="
call P1 GET  /api/patients
call P2 POST /api/patients "{\"name\":\"sweep_p_${STAMP}\",\"phone\":\"0000111122\"}"
NEW_PATIENT_ID=$(json_path 'data.id'); [[ -z "$NEW_PATIENT_ID" ]] && NEW_PATIENT_ID=$(json_path 'id')
call P3 GET  /api/patients/1
call P4 PATCH /api/patients/1 "{\"name\":\"updated-p-${STAMP}\"}"

# Phase 4: CARINGTYPES
hr; echo "=== PHASE 4: CARINGTYPES ==="
call CT1 GET    /api/caringtypes
call CT2 POST   /api/caringtypes "{\"name\":\"sweep_ct_${STAMP}\",\"duration\":30}"
NEW_CT_ID=$(json_path 'data.id'); [[ -z "$NEW_CT_ID" ]] && NEW_CT_ID=$(json_path 'id')
call CT3 GET    /api/caringtypes/1
call CT4 PATCH  /api/caringtypes/1 "{\"name\":\"updated-ct-${STAMP}\"}"
call CT5 DELETE "/api/caringtypes/${NEW_CT_ID:-99999}"

# Phase 5: CARINGS
hr; echo "=== PHASE 5: CARINGS ==="
call C1 GET  /api/carings
call C2 GET  /api/carings/1
call C3 GET  /api/carings/nurse/1
call C4 GET  /api/carings/patient/1
call C5 GET  /api/carings/caringtype/1
call C6 GET  /api/carings/trashed
call C7 POST /api/carings "{\"time\":\"2:30 PM\",\"nurse_id\":1,\"caringtype_id\":1,\"patient_id\":1,\"description\":\"sweep\",\"is_finished\":0}"
NEW_CARING_ID=$(json_path 'data.id'); [[ -z "$NEW_CARING_ID" ]] && NEW_CARING_ID=$(json_path 'id')
call C8 PATCH "/api/carings/${NEW_CARING_ID:-1}" "{\"is_finished\":1}"
call C9 DELETE "/api/carings/${NEW_CARING_ID:-99999}"

# Phase 6: ORPHANED ROUTE
hr; echo "=== PHASE 6: ORPHANED ROUTE CHECK ==="
call ORPHAN-GET  GET  /api/nurse
call ORPHAN-POST POST /api/nurse '{"test":1}'

hr
echo
echo "=== SUMMARY ==="
echo "Full log: $LOG"

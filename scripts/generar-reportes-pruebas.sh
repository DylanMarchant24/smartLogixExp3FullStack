#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

printf '\n== Backend: pruebas + JaCoCo ==\n'
for module in bff ms-inventario ms-pedidos ms-envios; do
  printf '\n-- %s --\n' "$module"
  (cd "$ROOT_DIR/$module" && mvn clean test jacoco:report)
done

printf '\n== Frontend: pruebas + coverage ==\n'
(cd "$ROOT_DIR/frontend" && npm install && npm run test:coverage)

printf '\nReportes generados:\n'
printf ' - bff/target/site/jacoco/index.html\n'
printf ' - ms-inventario/target/site/jacoco/index.html\n'
printf ' - ms-pedidos/target/site/jacoco/index.html\n'
printf ' - ms-envios/target/site/jacoco/index.html\n'
printf ' - frontend/coverage/lcov-report/index.html\n'

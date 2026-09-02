#!/usr/bin/env bash
# Arranca el backend cargando las variables de entorno desde .env
# Uso: ./run-dev.sh
set -a
# shellcheck disable=SC1091
source "$(dirname "$0")/.env"
set +a
exec ./mvnw spring-boot:run

#!/usr/bin/env bash
# Genera una CA propia (PKI minima) y un certificado de servidor firmado por ella,
# empaquetado en PKCS12 para Spring Boot (perfil "ssl").
# Uso: SSL_KEYSTORE_PASSWORD=secreto ./scripts/generar-certificado.sh [dominio]
set -euo pipefail

DOMINIO="${1:-localhost}"
PASS="${SSL_KEYSTORE_PASSWORD:?define SSL_KEYSTORE_PASSWORD}"
OUT="certs"
mkdir -p "$OUT"

# 1) Autoridad certificadora (CA)
openssl genrsa -out "$OUT/ca.key" 4096
openssl req -x509 -new -nodes -key "$OUT/ca.key" -sha256 -days 1825 \
  -subj "/C=PE/O=Gadbacorp/CN=Gadbacorp Minimarket CA" -out "$OUT/ca.crt"

# 2) Certificado del servidor con SAN, firmado por la CA
openssl genrsa -out "$OUT/server.key" 2048
openssl req -new -key "$OUT/server.key" -subj "/C=PE/O=Gadbacorp/CN=$DOMINIO" -out "$OUT/server.csr"
printf "subjectAltName=DNS:%s,DNS:localhost,IP:127.0.0.1\nextendedKeyUsage=serverAuth\n" "$DOMINIO" > "$OUT/san.ext"
openssl x509 -req -in "$OUT/server.csr" -CA "$OUT/ca.crt" -CAkey "$OUT/ca.key" -CAcreateserial \
  -out "$OUT/server.crt" -days 365 -sha256 -extfile "$OUT/san.ext"

# 3) Keystore PKCS12 (cadena completa) que consume Spring Boot
openssl pkcs12 -export -in "$OUT/server.crt" -inkey "$OUT/server.key" -certfile "$OUT/ca.crt" \
  -name minimarket -out "$OUT/minimarket.p12" -passout "pass:$PASS"

echo "Listo: $OUT/minimarket.p12  (instala $OUT/ca.crt como CA de confianza en los clientes)"
echo "Arranque: SSL_KEYSTORE_PASSWORD=*** java -jar app.jar --spring.profiles.active=ssl"

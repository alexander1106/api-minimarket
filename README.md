# api-minimarket

Backend REST para la gestión de minimarkets (ventas, inventario, caja, compras, delivery, seguridad).
Spring Boot 3.4 · Java 17 · Maven · MySQL / SQL Server · JWT · Kafka · WebFlux · Quarkus.

## Arquitectura

```
Frontend ──HTTP/JWT──▶ api-minimarket (Spring Boot, :8080) ──JPA──▶ MySQL | SQL Server
                          │  registra venta (transaccional)
                          ├──SSE──▶ GET /api/minimarket/ventas/eventos   (Reactor Flux)
                          └──Kafka topic "venta-creada"──▶ quarkus-reportes (:8081)
                                                          GET /reportes/ventas/resumen
```

| Pieza | Descripcion |
|---|---|
| `src/` | API principal (Spring Core, MVC, Data JPA, Security). Capas controller → service → repository |
| `RegistroVentaService` | Caso de uso de venta: venta + detalles + descuento de stock + pago en **una transaccion** |
| `messaging/` | Eventos de venta: Kafka (opcional) + stream reactivo SSE |
| `ApiCliente` / `ApiService` | Consumo de APIs externas (RENIEC/SUNAT) con **WebClient** no bloqueante |
| `quarkus-reportes/` | Microservicio **Quarkus + Gradle**: consume Kafka y expone reportes |
| `Jenkinsfile` | Pipeline CI: build, tests, cobertura JaCoCo, empaquetado, imagenes Docker |
| `docker-compose.yml` | MySQL (o SQL Server), Kafka, API, reportes y Jenkins (perfil `ci`) |

## Ejecutar

```bash
cp .env.example .env            # completar DB_PASSWORD, JWT_SECRET, ...
./mvnw spring-boot:run          # local (necesita MySQL)
docker compose up --build       # todo el stack
```

Swagger UI: `http://localhost:8080/swagger-ui.html` · Health: `/actuator/health`

### Perfiles

| Perfil | Uso |
|---|---|
| (por defecto) | MySQL |
| `sqlserver` | SQL Server (`--spring.profiles.active=sqlserver`, define `DB_URL`/`DB_USERNAME`/`DB_PASSWORD`) |
| `ssl` | HTTPS con keystore PKCS12 (`scripts/generar-certificado.sh`) |

Se combinan: `--spring.profiles.active=sqlserver,ssl`. En produccion usa `DDL_AUTO=validate`, `SHOW_SQL=false` y `LOG_SQL=WARN`.

## Seguridad

- **JWT** (HS256, secreto en `JWT_SECRET`, >= 32 caracteres) y **BCrypt** para contraseñas.
- **OAuth2 Resource Server** opcional: `OAUTH2_ENABLED=true` + `OAUTH2_ISSUER_URI=<issuer de Keycloak/Auth0/Azure AD>`;
  acepta tokens del IdP junto a los JWT propios.
- **HTTPS / certificados / PKI**: `scripts/generar-certificado.sh` crea una CA propia y un certificado de servidor (SAN) en PKCS12.
- **Cifrado**: `CryptoService` (AES-256-GCM, clave en `APP_CRYPTO_KEY`; generar con `openssl rand -base64 32`).
- **Roles** (`app.security.enforce-roles`): escribir (POST/PUT/DELETE) exige el rol del modulo; consultar (GET) cualquier usuario autenticado. Ver `SecurityConfig.reglasPorRol`.
- CORS configurable (`CORS_ALLOWED_ORIGINS`), sesiones stateless, **sin secretos en el repositorio**.

## Panel de suscripciones (SUPERADMIN)

`GET /empresas` devuelve, ademas de los datos de siempre, plan, ciclo, ingreso mensual (MRR), vencimiento, usuarios usados/limite,
sucursales y estado (ACTIVO, PRUEBA, VENCIDO, SUSPENDIDO). Acciones: `POST /empresas/{id}/suspender|reactivar|verificar-sunat`,
`PUT /empresas/{id}/suscripcion` (plan, ciclo, vencimiento, prueba) y `GET|POST|PUT /planes`. El plan limita usuarios y sucursales (409).

## Recuperacion de contraseña

`POST /enviar-link-recuperacion {"email"}` genera un token de 1 hora y envia `FRONTEND_URL/reset-password?token=...`
(requiere `MAIL_HOST`, `MAIL_USERNAME`, `MAIL_PASSWORD`). `POST /reset-password {"token","nuevaClave"}` cambia la clave.

## Pruebas

```bash
./mvnw verify                   # JUnit 5 + Mockito + MockMvc (H2 en memoria) + informe JaCoCo
./mvnw verify -Pintegracion     # contra servicios reales con Docker: Kafka y SQL Server (Testcontainers)
(cd quarkus-reportes && ./gradlew test)
```

Informe de cobertura: `target/site/jacoco/index.html`.

## Mensajeria (Kafka)

`KAFKA_ENABLED=true` y `KAFKA_BOOTSTRAP_SERVERS=host:9092`. Al confirmarse la transaccion de una venta se publica en `venta-creada`
un JSON con `idVenta, nroComprobante, tipoComprobante, total, idCliente, idSucursal, fecha`. Un fallo de Kafka no revierte la venta.

## Mapa contra el perfil buscado

| Requisito | Donde |
|---|---|
| Spring Boot/Core/MVC/Data/Security | API principal |
| APIs REST (desarrollo y consumo) | ~45 controllers + WebClient |
| Microservicios | API + `quarkus-reportes` comunicados por Kafka (ver `docs/ARQUITECTURA.md`) |
| SQL Server / relacionales | perfil `sqlserver` + MySQL |
| Git/GitHub | este repo |
| JUnit, Mockito | `src/test` |
| Scrum | `docs/SCRUM.md` |
| Quarkus | `quarkus-reportes/` |
| Jenkins | `Jenkinsfile` + servicio `jenkins` en compose |
| JWT, OAuth2, certificados, PKI, cifrado | seccion Seguridad |
| Maven y Gradle | Maven (API), Gradle (Quarkus) |
| Kafka | `messaging/` y `quarkus-reportes` |
| WebFlux | `WebClient`, `Mono`/`Flux`, SSE |
| Conocimientos en Host | `host/`: archivo batch EBCDIC de registros fijos con COMP-3 y totales de control (ver `docs/ARQUITECTURA.md`) |

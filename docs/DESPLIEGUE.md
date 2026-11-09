# Despliegue en la nube (Railway + MySQL + Netlify)

Backend y MySQL en **Railway**, frontend Angular en **Netlify**. Tiempo estimado: 30-40 minutos.

> Render tambien sirve para la API (`render.yaml`), pero **no ofrece MySQL**: habria que usar una base externa. Con Railway todo queda en un solo lugar.

## 0. Antes de empezar

1. El proyecto debe estar en GitHub (backend y frontend en repositorios separados o en el mismo).
2. **No subas credenciales.** Las claves que estuvieron en el historial de git (base de datos, Gmail, tokens de API) deben considerarse filtradas: cambialas.
3. Prepara estos valores:

| Variable | Como generarla |
|---|---|
| `JWT_SECRET` | Cualquier texto largo y aleatorio (32 caracteres o mas): `openssl rand -base64 48` |
| `APP_CRYPTO_KEY` | 32 bytes en Base64: `openssl rand -base64 32` |
| `APP_SEED_ADMIN_PASSWORD` | Contrasena del superadmin (usuario `alez`) |
| `APP_SEED_EMPRESA_PASSWORD` | Contrasena del usuario de ejemplo `empresa` |
| `CORS_ALLOWED_ORIGINS` | La URL exacta del frontend, p. ej. `https://tu-minimarket.netlify.app` |

Con `SPRING_PROFILES_ACTIVE=prod` **la API no arranca** si dejas la clave JWT por defecto, no defines `APP_CRYPTO_KEY`, dejas `1234` en las contrasenas de los usuarios iniciales o permites CORS con `*`.

## 1. Backend + MySQL en Railway

1. En [railway.com](https://railway.com) crea un proyecto: **New Project -> Deploy from GitHub repo** y elige el repositorio del backend. Railway detecta `railway.json` y construye con el `Dockerfile`.
2. En el mismo proyecto: **New -> Database -> Add MySQL**.
3. En el servicio de la API, pestana **Variables**:
   - `SPRING_PROFILES_ACTIVE` = `prod`
   - Enlaza la base: **Add Variable Reference** para `MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER` y `MYSQLPASSWORD` del servicio MySQL (la API las lee sola; no hace falta `DB_URL`).
   - `JWT_SECRET`, `APP_CRYPTO_KEY`, `APP_SEED_ADMIN_PASSWORD`, `APP_SEED_EMPRESA_PASSWORD`, `CORS_ALLOWED_ORIGINS` (valores del paso 0).
   - `FRONTEND_URL` = la misma URL del frontend (la usan los enlaces de los correos).
   - Opcionales: `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD` (recuperar contrasena), `APIS_TOKEN_RUC`, `APIS_TOKEN_DNI`.
   - `APP_SEED_DEMO` = `true` solo si quieres datos de demostracion para mostrar el sistema (por defecto en `prod` va apagado).
4. **Settings -> Networking -> Generate Domain.** Obtendras algo como `https://api-minimarket-production.up.railway.app`.
5. Imagenes subidas (logos, fotos de productos): el disco del contenedor se borra en cada despliegue. En **Settings -> Volumes** crea un volumen montado en `/app/uploads`.
6. Comprueba: `https://TU-DOMINIO/actuator/health` debe responder `{"status":"UP"}`. El primer arranque crea las tablas y los usuarios iniciales; tarda 1-2 minutos.

El usuario administrador de la plataforma es **`alez`** con la contrasena que pusiste en `APP_SEED_ADMIN_PASSWORD`.

## 2. Frontend en Netlify

1. En `src/app/components/link.ts` reemplaza `URL_PRODUCCION` por tu dominio de Railway **con** `/api/minimarket`:
   ```ts
   const URL_PRODUCCION = 'https://api-minimarket-production.up.railway.app/api/minimarket';
   ```
   En `localhost` el frontend sigue usando `http://localhost:8080` sin cambiar nada.
2. Sube el cambio a GitHub.
3. En [netlify.com](https://netlify.com): **Add new site -> Import from Git**. `netlify.toml` ya trae el comando (`npm run build`), la carpeta publicada (`dist/sistema-clinico-reservas/browser`) y la redireccion a `index.html` para las rutas de Angular.
4. Con la URL que te de Netlify, vuelve a Railway y actualiza `CORS_ALLOWED_ORIGINS` y `FRONTEND_URL` (si no, el navegador bloqueara las peticiones).

## 3. Despues de publicar

- Entra con `alez`, cambia lo que haga falta y crea la primera empresa y su usuario.
- **Copias de seguridad:** el plan de Railway no sustituye un respaldo propio. Desde tu maquina: `mysqldump -h <host-publico> -P <puerto> -u root -p railway > respaldo.sql` (el host y puerto publicos estan en el servicio MySQL, pestana Connect).
- **Plan gratuito o de prueba:** puede dormir el servicio o tener limites de horas; la primera peticion tarda en responder.
- **Migraciones:** el esquema se actualiza solo (`DDL_AUTO=update`). Antes de cambiar el modelo en produccion haz una copia.

## Problemas frecuentes

| Sintoma | Causa |
|---|---|
| La API cae al arrancar con "Configuracion insegura para produccion" | Falta alguna variable del paso 0; el mensaje dice cual |
| `Communications link failure` | No se enlazaron las variables `MYSQL*` o la base aun no esta lista |
| El login responde pero el navegador dice "CORS" | `CORS_ALLOWED_ORIGINS` no coincide exactamente con la URL del frontend (sin `/` al final) |
| Las rutas del frontend dan 404 al recargar | Falta la redireccion a `index.html` (`netlify.toml` / `public/_redirects`) |
| Las imagenes desaparecen tras un despliegue | No hay volumen en `/app/uploads` |

## Render (alternativa para la API)

1. **New -> Blueprint** y elige el repositorio: usa `render.yaml`.
2. Pega `DB_URL` (formato `jdbc:mysql://HOST:PUERTO/BASE?useSSL=false&allowPublicKeyRetrieval=true`), `DB_USERNAME` y `DB_PASSWORD` de una base MySQL externa (Railway, Aiven, TiDB Cloud).
3. Completa el resto de variables como en el paso 1.3. El plan gratuito duerme el servicio tras 15 minutos sin uso.

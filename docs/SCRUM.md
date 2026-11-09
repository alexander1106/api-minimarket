# Scrum en este proyecto

## Roles
- **Product Owner**: prioriza el backlog (ventas, caja, inventario).
- **Scrum Master**: facilita ceremonias y quita bloqueos.
- **Equipo de desarrollo**: entrega incrementos funcionales y probados.

## Cadencia (sprints de 2 semanas)
| Ceremonia | Cuando | Resultado |
|---|---|---|
| Planning | dia 1 | Sprint Goal + sprint backlog |
| Daily | diaria (15 min) | ayer / hoy / bloqueos |
| Review | ultimo dia | demo del incremento |
| Retrospectiva | ultimo dia | 1-3 mejoras accionables |

## Herramientas
- Issues y **GitHub Projects** (columnas: Backlog · Sprint · En curso · Review · Hecho). Etiquetas: `feature`, `bug`, `tech-debt`, `security`.
- Ramas: `main` estable, `feature/<issue>-descripcion`, PR con revision y CI (Jenkins) en verde.
- Commits: `tipo(modulo): descripcion` (ej. `feat(ventas): publicar evento venta-creada`).

## Definition of Done
- [ ] Codigo revisado en PR
- [ ] Tests unitarios (JUnit/Mockito) y `./mvnw verify` en verde
- [ ] Sin secretos en el codigo; configuracion por variables de entorno
- [ ] Endpoint documentado en Swagger
- [ ] Desplegable via `docker compose up`

## Backlog inicial sugerido
1. Autorizacion por rol en endpoints sensibles (`security`)
2. Correlativo de comprobante concurrente-seguro (`bug`)
3. Flyway para migraciones (`tech-debt`)
4. Paginacion en listados grandes (`feature`)
5. Microservicio de notificaciones (`feature`)

## Plantillas en el repositorio
- `.github/ISSUE_TEMPLATE/historia_de_usuario.md` y `bug.md`: cada elemento del backlog es un issue con criterios de aceptacion.
- `.github/pull_request_template.md`: la Definition of Done como lista de comprobacion en cada PR.
- Tablero: GitHub Projects con las columnas Backlog, Sprint, En curso, Review y Hecho.

> Las plantillas y el proceso estan definidos; el registro real de sprints depende del equipo que lo use.

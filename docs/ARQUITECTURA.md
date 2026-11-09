# Arquitectura y decisiones

## Estilo

La API principal sigue siendo un **monolito modular** (modulos: administrable, seguridad, inventario, ventas, compras, caja, delivery).
Los servicios que no necesitan consistencia transaccional con la venta se extraen como microservicios que reaccionan a **eventos**:

- `quarkus-reportes` (implementado): consume `venta-creada` y mantiene el resumen de ventas.
- Candidatos siguientes: `notificaciones` (correo/WhatsApp al facturar) e `inventario` (descuento asincrono de stock).
  El descuento de stock se mantiene dentro de la transaccion de venta a proposito: evita vender sin stock.

## Contrato del evento `venta-creada`

```json
{ "idVenta": 42, "nroComprobante": "B-000042", "tipoComprobante": "BOLETA",
  "total": 59.9, "idCliente": 7, "idSucursal": 1, "fecha": "2026-10-17" }
```

Se publica **despues del commit** (`TransactionSynchronization.afterCommit`), nunca antes; si Kafka falla se registra el error y la venta permanece.

## Reactivo y bloqueante

El API usa Spring MVC (servlet) por JPA, que es bloqueante. WebFlux se usa donde aporta: `WebClient` hacia RENIEC/SUNAT
(el endpoint devuelve `Mono<String>`) y `Flux` + SSE para ventas en vivo. Mezclarlos en un mismo servicio MVC es soportado.

## Integracion con Host (mainframe)

Intercambio batch de ventas con un Host, como en un proceso nocturno (paquete `host`):

- `GET /api/minimarket/host/ventas/exportar?fecha=AAAA-MM-DD[&idSucursal=n]` genera `VENTAS.AAAAMMDD.EBC`.
- `POST /api/minimarket/host/ventas/validar` (multipart `archivo`) valida un archivo recibido y devuelve su contenido.
- Formato: registros de **60 bytes de longitud fija**, codificacion **EBCDIC (IBM037)**, numericos *zoned decimal* y
  importes **COMP-3** (decimal empaquetado, `PIC S9(9)V99`). Estructura `H` (cabecera) + `D` (una por venta) + `T` (totales).
- Los **totales de control** (cantidad e importe) del registro `T` se verifican al leer: si no cuadran con lo recibido,
  el archivo se rechaza. El copybook COBOL esta documentado en `RegistroHost`.
- El transporte (SFTP, IBM MQ o Kafka) queda fuera del API: el adaptador solo produce y valida el archivo.
- Alcance: es un adaptador de formato probado con tests (vectores COMP-3 de COBOL, ida y vuelta, deteccion de archivos
  alterados). No se probo contra un mainframe real.

## Unidades de medida y presentaciones

- Cada producto tiene una **unidad base** (UND, KG, LT, MT). El **stock siempre se guarda en unidad base** y con 3 decimales.
- `unidad_medida.permite_decimales` indica si admite fracciones: KG y LT si (2.5 kg), UND no (no se vende 1.5 unidades).
- Una **presentacion** (`producto_presentacion`) es una forma de comprar o vender el producto con un **factor** a unidad base:
  "Saco 50 kg" (factor 50), "Paquete x12" (factor 12). El precio puede ir por presentacion.
- Las lineas de venta, cotizacion y compra guardan lo que se pidio (`cantidad`, `presentacion`) y lo que mueve el stock
  (`cantidad_base = cantidad x factor`). `ConversionUnidadesService` convierte y valida; `StockService` descuenta.
- Ajustes, traslados, ingreso de stock y devoluciones se expresan directamente en unidad base.
- Migracion para bases anteriores: `docs/migraciones/001_cantidades_decimales.sql`.

## Compras y stock

- Una compra **suma stock** en su sucursal (en unidad base): va al primer inventario donde ya esta el producto, o lo crea
  en el primer inventario de la sucursal. Sin inventario en la sucursal la compra se rechaza (409).
- Editar o quitar lineas devuelve la diferencia; anular la compra devuelve todo (409 si ya se vendio y no alcanza).
- Crear, editar y anular una compra son atomicos (`@Transactional` con rollback).
- `GET /productos/stock-bajo[?idSucursal=]` lista los productos con stock <= `stockMinimo`.
- Catalogos globales (solo SUPERADMIN escribe): unidades de medida, categorias y tipos de producto.

## Aislamiento por empresa (multi-tenant)

- Cada empresa solo ve y modifica lo suyo; el SUPERADMIN no esta restringido. Paquete `tenant`:
  `TenantRules` (de que empresa es cada dato), `TenantGuard` (URL y cuerpo), `TenantWebConfig` (interceptor + filtros de entrada/salida).
- Una URL o respuesta de otra empresa es 404; guardar apuntando a una sucursal ajena es 403.
- Los productos tienen `id_empresa`: cada empresa tiene su catalogo. Un dato sin empresa solo lo ve el SUPERADMIN.

## Kardex e IGV

- `movimiento_stock` (kardex): cada entrada o salida de stock (compra, venta, anulaciones, devolucion, ajuste, traslado)
  queda con tipo, cantidad en unidad base con signo, stock resultante, referencia y usuario. `GET /kardex/productos/{id}?idSucursal=&desde=&hasta=`.
- IGV: los precios incluyen IGV (`app.igv.tasa`, 18 %). La venta guarda `opGravada`, `opInafecta` e `igv`; los productos con
  `afectoIgv=false` son inafectos. El PDF del comprobante muestra el desglose.

## Lotes y vencimientos

- Una linea de compra puede traer `lote` y `fechaVencimiento`; crea un `lote_producto` con su saldo (unidad base).
  No se acepta una fecha anterior a hoy.
- Las salidas de stock consumen primero el lote que vence antes (FEFO). Lo que no tiene lote (stock sin lote) no se toca.
- Quitar o corregir la linea de compra ajusta su lote. Devoluciones y anulaciones de venta no reabren lotes.
- `GET /lotes/por-vencer?dias=30&idSucursal=` y `GET /lotes/productos/{id}`.

## Notificaciones

- Cada aviso es de un usuario (`notificaciones.id_usuario`): un hecho que interesa a varios genera una fila por usuario, asi cada quien
  marca como leidas las suyas. El mismo hecho (misma `clave`) no se repite mientras siga sin leerse.
- Los genera el sistema: stock bajo o agotado al descontar stock (usuarios con el modulo Inventario), lotes por vencer o vencidos
  (revision diaria a las 7:00 y al arrancar) y suscripciones por vencer (para el SUPERADMIN). Un fallo al avisar nunca rompe la operacion.
- API: `GET /notificaciones`, `/contador`, `PUT /{id}/leida`, `/{id}/no-leida`, `/marcar-todas`, `DELETE /{id}`, `/leidas`.
- Las leidas se depuran a los 30 dias. Configurable: `app.notificaciones.programadas`, `.cron`, `.dias-vencimiento`, `.dias-suscripcion`.

## Auditoria

- `AuditoriaFilter` (dentro de la cadena de seguridad, despues de la autorizacion) registra en `auditoria` las operaciones que
  modifican o borran datos (por defecto solo EDITAR y ELIMINAR; `app.auditoria.acciones` permite agregar CREAR, INICIO_SESION,
  INICIO_SESION_FALLIDO u OTRO): usuario, rol, empresa, accion, modulo, recurso, id del registro, IP, navegador,
  duracion, resultado HTTP y los datos enviados. Las contrasenas y tokens se guardan como `***` y los textos largos se acortan.
- Solo lo consulta el SUPERADMIN: `GET /auditoria`, `/auditoria/{id}`, `/auditoria/resumen`, `/auditoria/filtros`.
- Un fallo al auditar nunca rompe la operacion. Los intentos que el sistema rechaza por falta de permiso (401/403) no se registran.
- No se auditan las consultas (GET), los avisos de rutina (notificaciones) ni la subida suelta de imagenes.
- Retencion: `app.auditoria.retencion-dias` (365). Depuracion diaria a las 3:30.

## Cajas y turnos

- Una caja (puesto fisico) tiene un solo turno abierto a la vez y un usuario tiene un solo turno abierto. Abrir exige que la caja
  este LIBRE y que caja y usuario sean de la misma sucursal (409 con el motivo: en uso por quien, malograda, o ya tienes una caja abierta).
  El estado del turno lo fija el servidor. Al arrancar, `ReparadorCajasRunner` libera las cajas OCUPADAS sin turno abierto.
- Una venta con `id_apertura_caja` exige un turno ABIERTO de la sucursal de la venta. Un cajero cobra solo en su propio turno;
  ADMIN, EMPRESA y SUPERVISOR pueden cobrar en cualquier turno abierto de su sucursal (409 si no).
- La venta guarda `vendedor`, `idVendedor` e `idAperturaCaja`. `GET /aperturas-cajas/sucursal/{id}/abiertas` lista los turnos abiertos
  (con `esMia`) y `GET /aperturas-cajas/{id}/vendedores` resume las ventas del turno por vendedor.
- `GET /productos/codigo/{codigo}` resuelve lo que lee el lector: codigo de barras, SKU o codigo de barras de una presentacion.

## Reglas de negocio en el servidor

| Regla | Donde |
|---|---|
| Venta + detalles + stock + pago (+ delivery + ingreso en caja opcionales) en **una** transaccion | `RegistroVentaService` |
| Movimientos de caja (saldo total, efectivo y por metodo de pago) con la apertura bloqueada | `CajaMovimientoService` |
| Pago de cotizacion: conversion a venta, comprobante, pago e ingreso a caja, todo o nada | `PagoCotizacionService` |
| Arqueo (el monto contado debe coincidir con el efectivo del sistema) y cierre (exige arqueo) | `CierreCajaService` |
| Comprobantes `B-/F-` y cotizaciones `COT-` correlativos sin duplicados (fila `secuencias` bloqueada) | `NumeracionService` |
| Autorizacion por rol en operaciones de escritura (SUPERADMIN, ADMIN, SUPERVISOR, CAJERO, ALMACENERO, EMPRESA) | `SecurityConfig.reglasPorRol` |

## Pendiente conocido

- Un usuario ve datos de todas las sucursales/empresas: falta aislar por `idSucursal`/`idEmpresa` del token.
- Migraciones de esquema con Flyway en lugar de `ddl-auto=update` (requiere baseline sobre la BD actual).
- Paginacion en el servidor para listados grandes.
- Las validaciones son manuales (clientes, ventas, caja); no se usa `@Valid` con anotaciones en los DTOs.
- Definir que guarda `empresa.logo` (hoy el frontend lo usa como imagen y como color).

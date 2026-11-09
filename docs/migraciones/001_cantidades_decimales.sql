-- Migracion 001: cantidades y stock con decimales (kg, litros) y presentaciones de producto.
--
-- Cuando hace falta: bases de datos que se crearon ANTES de este cambio y que NO arrancaron con el codigo nuevo.
-- (Con spring.jpa.hibernate.ddl-auto=update las tablas/columnas nuevas se crean solas al arrancar, pero conviene
-- confirmar que las cantidades quedaron como DECIMAL(14,3) y no como INT.)
--
-- Es idempotente: MODIFY a DECIMAL(14,3) puede ejecutarse varias veces. Los enteros existentes se conservan (5 -> 5.000).
-- Hacer copia antes:  mysqldump -u root rapimarket_minimarket > respaldo.sql

ALTER TABLE inventario_producto            MODIFY stockactual DECIMAL(14,3);
ALTER TABLE detalles_ventas                MODIFY cantidad    DECIMAL(14,3);
ALTER TABLE detalles_cotizaciones          MODIFY cantidad    DECIMAL(14,3);
ALTER TABLE detalle_compras                MODIFY cantidad    DECIMAL(14,3);
ALTER TABLE traslado_inventario            MODIFY cantidad    DECIMAL(14,3);
ALTER TABLE ajuste_inventario              MODIFY cantidad    DECIMAL(14,3);
ALTER TABLE detalles_devoluciones          MODIFY cantidad    DECIMAL(14,3);
ALTER TABLE detalles_devoluciones_compras  MODIFY cantidad    DECIMAL(14,3);

-- Las lineas anteriores a las presentaciones no tienen cantidad_base: el sistema usa entonces la propia cantidad
-- (equivale a "unidad base"). Si se quiere dejarlo explicito:
UPDATE detalles_ventas       SET cantidad_base = cantidad WHERE cantidad_base IS NULL;
UPDATE detalles_cotizaciones SET cantidad_base = cantidad WHERE cantidad_base IS NULL;
UPDATE detalle_compras       SET cantidad_base = cantidad WHERE cantidad_base IS NULL;

-- Unidades de medida: marcar las que admiten fracciones (el arranque ya siembra UND, KG, LT y MT).
UPDATE unidad_medida SET permite_decimales = 1 WHERE codigo IN ('KG', 'LT', 'MT');
-- Las unidades existentes creadas a mano (p. ej. "Kilo", "Litro") se completan desde la pantalla de unidades de medida.

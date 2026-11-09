package com.gadbacorp.api.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.entity.inventario.Categorias;
import com.gadbacorp.api.entity.inventario.TipoProducto;
import com.gadbacorp.api.entity.ventas.MetodoPagoCatalogo;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.repository.inventario.CategoriasRepository;
import com.gadbacorp.api.repository.inventario.TipoProductoRepository;
import com.gadbacorp.api.repository.ventas.MetodoPagoCatalogoRepository;
import com.gadbacorp.api.repository.ventas.MetodosPagoRepository;

/**
 * Catalogos globales de la plataforma que administra el SUPERADMIN: metodos de pago, categorias y tipos de producto.
 * Solo agrega lo que falta (por nombre), asi que se puede ejecutar en cada arranque. Se ejecuta despues del seed base.
 */
@Component
@Order(20)
public class DatosCatalogosRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosCatalogosRunner.class);

    private final boolean habilitado;
    private final MetodoPagoCatalogoRepository catalogo;
    private final MetodosPagoRepository metodosPago;
    private final CategoriasRepository categorias;
    private final TipoProductoRepository tipos;

    public DatosCatalogosRunner(@Value("${app.seed.enabled:true}") boolean habilitado,
                                MetodoPagoCatalogoRepository catalogo, MetodosPagoRepository metodosPago,
                                CategoriasRepository categorias, TipoProductoRepository tipos) {
        this.habilitado = habilitado;
        this.catalogo = catalogo;
        this.metodosPago = metodosPago;
        this.categorias = categorias;
        this.tipos = tipos;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!habilitado) {
            return;
        }
        asegurarMetodosDePago();
        vincularMetodosExistentes();
        asegurarCategorias();
        asegurarTipos();
    }

    private void asegurarMetodosDePago() {
        Object[][] datos = {
                {"Efectivo", "EFECTIVO", false},
                {"Yape", "DIGITAL", true},
                {"Plin", "DIGITAL", true},
                {"Tarjeta", "TARJETA", false},
                {"Transferencia", "TRANSFERENCIA", true},
        };
        for (Object[] d : datos) {
            if (catalogo.findByNombreIgnoreCase((String) d[0]).isEmpty()) {
                MetodoPagoCatalogo m = new MetodoPagoCatalogo();
                m.setNombre((String) d[0]);
                m.setTipo((String) d[1]);
                m.setRequiereDetalle((Boolean) d[2]);
                catalogo.save(m);
                log.info("Seed: metodo de pago del catalogo {}", d[0]);
            }
        }
    }

    /** Los metodos de pago que las sucursales ya tenian (texto libre) pasan a apuntar al catalogo por su nombre. */
    private void vincularMetodosExistentes() {
        for (MetodosPago m : metodosPago.findAll()) {
            if (m.getCatalogo() == null && m.getNombre() != null) {
                catalogo.findByNombreIgnoreCase(m.getNombre().trim()).ifPresent(c -> {
                    m.setCatalogo(c);
                    metodosPago.save(m);
                });
            }
        }
    }

    private void asegurarCategorias() {
        for (String nombre : List.of("Abarrotes", "Bebidas", "Lácteos", "Limpieza", "Snacks", "Frutas y verduras",
                "Carnes y embutidos", "Panadería", "Cuidado personal")) {
            if (categorias.findAll().stream().noneMatch(c -> nombre.equalsIgnoreCase(c.getNombre()))) {
                Categorias c = new Categorias();
                c.setNombre(nombre);
                categorias.save(c);
                log.info("Seed: categoria {}", nombre);
            }
        }
    }

    private void asegurarTipos() {
        for (String nombre : List.of("Perecible", "No perecible", "Granel", "Servicio")) {
            if (tipos.findAll().stream().noneMatch(t -> nombre.equalsIgnoreCase(t.getNombre()))) {
                TipoProducto t = new TipoProducto();
                t.setNombre(nombre);
                tipos.save(t);
                log.info("Seed: tipo de producto {}", nombre);
            }
        }
    }
}

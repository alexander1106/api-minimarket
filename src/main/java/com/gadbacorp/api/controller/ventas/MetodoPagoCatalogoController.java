package com.gadbacorp.api.controller.ventas;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.ventas.MetodoPagoCatalogo;
import com.gadbacorp.api.excepciones.OperacionNoPermitidaException;
import com.gadbacorp.api.excepciones.RecursoDuplicadoException;
import com.gadbacorp.api.excepciones.RegistroInvalidoException;
import com.gadbacorp.api.repository.ventas.MetodoPagoCatalogoRepository;
import com.gadbacorp.api.repository.ventas.MetodosPagoRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

/** Catalogo global de metodos de pago: todos lo consultan, solo el SUPERADMIN lo modifica. */
@Tag(name = "Ventas - Catalogo de metodos de pago")
@RestController
@RequestMapping("/api/minimarket/metodos-pago-catalogo")
public class MetodoPagoCatalogoController {

    private final MetodoPagoCatalogoRepository catalogo;
    private final MetodosPagoRepository metodosPago;

    public MetodoPagoCatalogoController(MetodoPagoCatalogoRepository catalogo, MetodosPagoRepository metodosPago) {
        this.catalogo = catalogo;
        this.metodosPago = metodosPago;
    }

    @GetMapping
    public List<MetodoPagoCatalogo> listar() {
        return catalogo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetodoPagoCatalogo> buscar(@PathVariable Integer id) {
        return catalogo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<MetodoPagoCatalogo> crear(@RequestBody MetodoPagoCatalogo cuerpo) {
        validar(cuerpo, null);
        MetodoPagoCatalogo nuevo = new MetodoPagoCatalogo();
        copiar(cuerpo, nuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogo.save(nuevo));
    }

    @PutMapping
    @Transactional
    public MetodoPagoCatalogo editar(@RequestBody MetodoPagoCatalogo cuerpo) {
        if (cuerpo.getIdCatalogo() == null) {
            throw new RegistroInvalidoException(Map.of("idCatalogo", "El id es obligatorio"));
        }
        MetodoPagoCatalogo existente = catalogo.findById(cuerpo.getIdCatalogo())
                .orElseThrow(() -> new java.util.NoSuchElementException("Metodo de pago no encontrado"));
        validar(cuerpo, existente.getIdCatalogo());
        copiar(cuerpo, existente);
        return catalogo.save(existente);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Map<String, String> eliminar(@PathVariable Integer id) {
        MetodoPagoCatalogo existente = catalogo.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Metodo de pago no encontrado"));
        if (metodosPago.existsByCatalogo_IdCatalogo(id)) {
            throw new OperacionNoPermitidaException(
                    "No se puede eliminar: hay sucursales que usan este metodo de pago");
        }
        catalogo.delete(existente);
        return Map.of("Detalle", "Metodo de pago eliminado del catalogo");
    }

    private void validar(MetodoPagoCatalogo cuerpo, Integer idActual) {
        Map<String, String> errores = new java.util.LinkedHashMap<>();
        if (cuerpo.getNombre() == null || cuerpo.getNombre().isBlank()) {
            errores.put("nombre", "El nombre es obligatorio");
        }
        if (cuerpo.getTipo() != null && !MetodoPagoCatalogo.TIPOS.contains(cuerpo.getTipo())) {
            errores.put("tipo", "El tipo debe ser uno de " + MetodoPagoCatalogo.TIPOS);
        }
        if (!errores.isEmpty()) {
            throw new RegistroInvalidoException(errores);
        }
        catalogo.findByNombreIgnoreCase(cuerpo.getNombre().trim()).ifPresent(otro -> {
            if (!otro.getIdCatalogo().equals(idActual)) {
                throw new RecursoDuplicadoException("Ya existe el metodo de pago " + cuerpo.getNombre());
            }
        });
    }

    private static void copiar(MetodoPagoCatalogo origen, MetodoPagoCatalogo destino) {
        destino.setNombre(origen.getNombre().trim());
        destino.setTipo(origen.getTipo() == null ? "OTRO" : origen.getTipo());
        destino.setRequiereDetalle(Boolean.TRUE.equals(origen.getRequiereDetalle()));
    }
}

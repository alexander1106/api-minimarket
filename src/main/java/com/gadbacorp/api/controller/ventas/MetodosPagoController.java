package com.gadbacorp.api.controller.ventas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import com.gadbacorp.api.entity.ventas.MetodoPagoCatalogo;
import com.gadbacorp.api.excepciones.RecursoDuplicadoException;
import com.gadbacorp.api.excepciones.RegistroInvalidoException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.service.ventas.IMetodosPagoService;
import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Ventas - Métodos de pago")
@RestController
@RequestMapping("/api/minimarket")
public class MetodosPagoController {

    @Autowired
    private IMetodosPagoService metodosPagoService;

    @Autowired
    private com.gadbacorp.api.repository.ventas.MetodosPagoRepository metodosPagoRepository;

    @Autowired
    private com.gadbacorp.api.repository.ventas.MetodoPagoCatalogoRepository catalogoRepository;

    @Autowired
    private com.gadbacorp.api.repository.administrable.SucursalesRepository sucursalesRepository;

    @GetMapping("/metodos-pago")
    public List<MetodosPago> buscarTodos() {
        return metodosPagoService.listarMetodosPago();
    }
    /** Compatibilidad: ya no se crean metodos de texto libre, se activan los del catalogo (por nombre). */
    @PostMapping("/metodos-pago")
    @Transactional
    public ResponseEntity<?> guardarMetodoPago(@RequestBody MetodosPago metodoPago) {
        if (metodoPago.getSucursal() == null || metodoPago.getSucursal().getIdSucursal() == null) {
            throw new RegistroInvalidoException(Map.of("sucursal", "La sucursal es obligatoria"));
        }
        return ResponseEntity.ok(activar(metodoPago.getSucursal().getIdSucursal(), metodoPago));
    }

    @GetMapping("/metodos-pago/{id}")
    public Optional<MetodosPago> buscarId(@PathVariable("id") Integer id){
        return metodosPagoService.obtenerMetodoPago(id);
    }

/** Solo se edita el dato propio de la empresa (numero de Yape, cuenta...); el nombre viene del catalogo. */
@PutMapping("/metodos-pago")
@Transactional
public ResponseEntity<Map<String, Object>> modificar(@RequestBody MetodosPago metodoPago) {
    MetodosPago existente = metodosPagoService.obtenerMetodoPago(metodoPago.getIdMetodoPago())
            .orElseThrow(() -> new java.util.NoSuchElementException("Metodo de pago no encontrado"));
    if (existente.getCatalogo() != null && Boolean.TRUE.equals(existente.getCatalogo().getRequiereDetalle())
            && (metodoPago.getDetalle() == null || metodoPago.getDetalle().isBlank())) {
        throw new RegistroInvalidoException(Map.of("detalle", "Indique el dato de " + existente.getNombre()));
    }
    existente.setDetalle(metodoPago.getDetalle() == null || metodoPago.getDetalle().isBlank()
            ? null : metodoPago.getDetalle().trim());
    metodosPagoService.editarMetodosPago(existente);

    Map<String, Object> respuesta = new HashMap<>();
    respuesta.put("status", 200);
    respuesta.put("Detalle", "Método de pago actualizado");
    return ResponseEntity.ok(respuesta);
}

   @DeleteMapping("/metodos-pago/{id}")
public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Integer id) {
    metodosPagoService.eliminarMetodoPago(id);
    
    Map<String, Object> respuesta = new HashMap<>();
    respuesta.put("status", 200);
    respuesta.put("Detalle", "Método de pago eliminado");
    
    return ResponseEntity.ok(respuesta);
}

    @GetMapping("/sucursales/{idSucursal}/metodos-pago")
public List<MetodosPago> listarPorSucursal(@PathVariable Integer idSucursal) {
    return metodosPagoService.listarPorSucursal(idSucursal);
}

/**
 * La sucursal activa un metodo del catalogo. Body: {"idCatalogo": 2, "detalle": "999 888 777"}
 * (tambien acepta {"nombre": "Yape"} por compatibilidad). 409 si ya lo tiene activo.
 */
@PostMapping({"/sucursales/{idSucursal}/metodos-pago", "/sucursales/{idSucursal}/metodos-pago/activar"})
@Transactional
public ResponseEntity<Map<String, Object>> guardarMetodoPagoEnSucursal(
        @PathVariable Integer idSucursal,
        @RequestBody Map<String, Object> cuerpo) {
    MetodosPago pedido = new MetodosPago();
    pedido.setNombre(cuerpo.get("nombre") == null ? null : String.valueOf(cuerpo.get("nombre")));
    pedido.setDetalle(cuerpo.get("detalle") == null ? null : String.valueOf(cuerpo.get("detalle")));
    if (cuerpo.get("idCatalogo") != null) {
        MetodoPagoCatalogo c = new MetodoPagoCatalogo();
        c.setIdCatalogo(Integer.valueOf(String.valueOf(cuerpo.get("idCatalogo"))));
        pedido.setCatalogo(c);
    }
    MetodosPago creado = activar(idSucursal, pedido);

    Map<String, Object> respuesta = new HashMap<>();
    respuesta.put("status", 200);
    respuesta.put("Detalle", "Método de pago activado");
    respuesta.put("idMetodoPago", creado.getIdMetodoPago());
    return ResponseEntity.ok(respuesta);
}

private MetodosPago activar(Integer idSucursal, MetodosPago pedido) {
    Sucursales sucursal = sucursalesRepository.findById(idSucursal)
            .orElseThrow(() -> new java.util.NoSuchElementException("Sucursal no encontrada"));
    MetodoPagoCatalogo catalogo = null;
    if (pedido.getCatalogo() != null && pedido.getCatalogo().getIdCatalogo() != null) {
        catalogo = catalogoRepository.findById(pedido.getCatalogo().getIdCatalogo()).orElse(null);
    } else if (pedido.getNombre() != null) {
        catalogo = catalogoRepository.findByNombreIgnoreCase(pedido.getNombre().trim()).orElse(null);
    }
    if (catalogo == null) {
        throw new RegistroInvalidoException(Map.of("idCatalogo",
                "Elija un metodo del catalogo. Si no esta, pidale al administrador de la plataforma que lo agregue"));
    }
    if (metodosPagoRepository.existsByCatalogo_IdCatalogoAndSucursal_IdSucursal(catalogo.getIdCatalogo(), idSucursal)) {
        throw new RecursoDuplicadoException("La sucursal ya tiene activo " + catalogo.getNombre());
    }
    if (Boolean.TRUE.equals(catalogo.getRequiereDetalle())
            && (pedido.getDetalle() == null || pedido.getDetalle().isBlank())) {
        throw new RegistroInvalidoException(Map.of("detalle", "Indique el dato de " + catalogo.getNombre()
                + " (numero, cuenta o alias)"));
    }
    MetodosPago nuevo = new MetodosPago();
    nuevo.setNombre(catalogo.getNombre());
    nuevo.setCatalogo(catalogo);
    nuevo.setDetalle(pedido.getDetalle() == null || pedido.getDetalle().isBlank() ? null : pedido.getDetalle().trim());
    nuevo.setSucursal(sucursal);
    nuevo.setEstado(1);
    return metodosPagoRepository.save(nuevo);
}

}

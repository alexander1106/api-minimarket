    package com.gadbacorp.api.controller.caja;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.CrossOrigin;
    import org.springframework.web.bind.annotation.DeleteMapping;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.PutMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    import com.gadbacorp.api.entity.caja.AperturaCaja;
    import com.gadbacorp.api.entity.caja.AperturaCajaDTO;
    import com.gadbacorp.api.entity.caja.Caja;
import com.gadbacorp.api.entity.caja.TransaccionesCaja;
    import com.gadbacorp.api.entity.empleados.Usuarios;
    import com.gadbacorp.api.repository.caja.AperturaCajaRepository;
    import com.gadbacorp.api.repository.caja.CajaRepository;
    import com.gadbacorp.api.repository.empleados.UsuarioRepository;
    import com.gadbacorp.api.service.caja.IAperturaCajaService;
import com.gadbacorp.api.service.caja.ITransaccionesCajaServices;

    @RestController
    @CrossOrigin("*")    
    @RequestMapping("/api/minimarket")
    public class AperturaCajaController {

        @Autowired
        private IAperturaCajaService aperturaCajaService;

        @Autowired
        private UsuarioRepository empleadosRepository;

        @Autowired
        private ITransaccionesCajaServices transaccionesCajaService;
        @Autowired
        private AperturaCajaRepository aperturaCajaRepository;

        @Autowired
        private CajaRepository cajaRepository;
        @GetMapping("/aperturas-cajas")
        public List<AperturaCaja> listarAperturasCajas() {
            return aperturaCajaService.listarAperturaCajas();
        }
        
        @GetMapping("/aperturas-cajas/{id}")
        public Optional<AperturaCaja> buscarCaja(@PathVariable Integer id) {
            return aperturaCajaService.buscarAperturaCaja(id);
        }

        @GetMapping("/aperturas-cajas/sucursal/{idSucursal}")
public ResponseEntity<?> listarAperturasPorSucursal(@PathVariable Integer idSucursal) {
    List<AperturaCaja> aperturas = aperturaCajaRepository.findByCajaSucursalesIdSucursal(idSucursal);

    if (aperturas.isEmpty()) {
        return ResponseEntity.ok(Collections.singletonMap("mensaje", "No se encontraron aperturas para esta sucursal."));
    }
    return ResponseEntity.ok(aperturas);
}
@GetMapping("/aperturas-cajas/{idApertura}/transacciones")
public ResponseEntity<List<TransaccionesCaja>> listarTransaccionesPorApertura(@PathVariable Integer idApertura) {
    List<TransaccionesCaja> transacciones = transaccionesCajaService.obtenerPorApertura(idApertura);
    return ResponseEntity.ok(transacciones);
}

@GetMapping("/aperturas-caja/usuario/{idUsuario}/abierta")
public ResponseEntity<?> obtenerCajaAbiertaPorUsuario(@PathVariable Integer idUsuario) {
    List<AperturaCaja> aperturas = aperturaCajaRepository
        .findByUsuarios_IdUsuarioAndEstadoCaja(idUsuario, "ABIERTA");

    if (aperturas.isEmpty()) {
        return ResponseEntity.ok().body(null); // o 404
    }

    // Elegir una sola si hay varias (por ejemplo la más reciente)
    AperturaCaja apertura = aperturas.get(0); // o usar un criterio más específico
    return ResponseEntity.ok(apertura);
}


     @GetMapping("/aperturas-cajas/{idAperturaCaja}/caja")
    public ResponseEntity<Caja> obtenerCajaPorApertura(@PathVariable Integer idAperturaCaja) {
        return aperturaCajaRepository.findById(idAperturaCaja)
                .map(apertura -> ResponseEntity.ok(apertura.getCaja()))
                .orElse(ResponseEntity.notFound().build());
    }

@PostMapping("/aperturas-cajas")
public ResponseEntity<Map<String, Object>> guardarAperturaCaja(@RequestBody AperturaCajaDTO dto) {
    Map<String, Object> respuesta = new HashMap<>();

    // Validaciones previas
    if (dto.getId_caja() == null) {
        respuesta.put("status", 400);
        respuesta.put("Detalle", "El ID de la caja no puede ser nulo.");
        return ResponseEntity.badRequest().body(respuesta);
    }
    if (dto.getId_empleado() == null) {
        respuesta.put("status", 400);
        respuesta.put("Detalle", "El ID del empleado no puede ser nulo.");
        return ResponseEntity.badRequest().body(respuesta);
    }
    if (dto.getFechaApertura() == null) {
        respuesta.put("status", 400);
        respuesta.put("Detalle", "La fecha de apertura no puede ser nula.");
        return ResponseEntity.badRequest().body(respuesta);
    }
    if (dto.getSaldoInicial() == null || dto.getSaldoInicial().doubleValue() < 0) {
        respuesta.put("status", 400);
        respuesta.put("Detalle", "El saldo inicial no puede ser nulo ni negativo.");
        return ResponseEntity.badRequest().body(respuesta);
    }

    // Buscar empleado y caja
    Optional<Usuarios> empleadoOptional = empleadosRepository.findById(dto.getId_empleado());
    if (empleadoOptional.isEmpty()) {
        respuesta.put("status", 400);
        respuesta.put("Detalle", "No se encontró el empleado con el ID proporcionado.");
        return ResponseEntity.badRequest().body(respuesta);
    }

    Optional<Caja> cajaOptional = cajaRepository.findById(dto.getId_caja());
    if (cajaOptional.isEmpty()) {
        respuesta.put("status", 400);
        respuesta.put("Detalle", "No se encontró la caja con el ID proporcionado.");
        return ResponseEntity.badRequest().body(respuesta);
    }

    Caja caja = cajaOptional.get();

    // Cambiar estado de la caja a "OCUPADA"
    caja.setEstadoCaja("OCUPADA");
    cajaRepository.save(caja);

    // Crear apertura de caja
    AperturaCaja aperturaCaja = new AperturaCaja();
    aperturaCaja.setFechaApertura(dto.getFechaApertura());
    aperturaCaja.setFechaCierre(dto.getFechaCierre());
    aperturaCaja.setSaldoInicial(dto.getSaldoInicial());
    aperturaCaja.setEstadoCaja(dto.getEstadoCaja());
    aperturaCaja.setSaldoFinal(dto.getSaldoFinal());
    aperturaCaja.setUsuarios(empleadoOptional.get());
    aperturaCaja.setSaldoEfectivo(0.0);
    aperturaCaja.setCaja(caja);

    aperturaCajaService.guardarAperturaCaja(aperturaCaja);

    respuesta.put("status", 200);
    respuesta.put("Detalle", "Apertura de caja registrada correctamente.");
    return ResponseEntity.ok(respuesta);
}


@PutMapping("/aperturas-cajas")
public ResponseEntity<Map<String, Object>> modificarAperturaCaja(@RequestBody AperturaCajaDTO dto) {
    Map<String, Object> respuesta = new HashMap<>();

    if (dto.getIdAperturaCaja() == null) {
        respuesta.put("status", 400);
        respuesta.put("Detalle", "El ID de la apertura de caja no puede ser nulo.");
        return ResponseEntity.badRequest().body(respuesta);
    }
    if (dto.getId_caja() == null) {
        respuesta.put("status", 400);
        respuesta.put("Detalle", "El ID de la caja no puede ser nulo.");
        return ResponseEntity.badRequest().body(respuesta);
    }
    if (dto.getId_empleado() == null) {
        respuesta.put("status", 400);
        respuesta.put("Detalle", "El ID del empleado no puede ser nulo.");
        return ResponseEntity.badRequest().body(respuesta);
    }

    Optional<AperturaCaja> aperturaExistente = aperturaCajaService.buscarAperturaCaja(dto.getIdAperturaCaja());
    if (aperturaExistente.isEmpty()) {
        respuesta.put("status", 404);
        respuesta.put("Detalle", "No se encontró una apertura de caja con el ID proporcionado.");
        return ResponseEntity.status(404).body(respuesta);
    }

    Optional<Caja> cajaOptional = cajaRepository.findById(dto.getId_caja());
    if (cajaOptional.isEmpty()) {
        respuesta.put("status", 404);
        respuesta.put("Detalle", "No se encontró la caja con el ID proporcionado.");
        return ResponseEntity.status(404).body(respuesta);
    }

    Optional<Usuarios> empleadoOptional = empleadosRepository.findById(dto.getId_empleado());
    if (empleadoOptional.isEmpty()) {
        respuesta.put("status", 404);
        respuesta.put("Detalle", "No se encontró el empleado con el ID proporcionado.");
        return ResponseEntity.status(404).body(respuesta);
    }

    if (dto.getSaldoInicial() == null || dto.getSaldoInicial().doubleValue() < 0) {
        respuesta.put("status", 400);
        respuesta.put("Detalle", "El saldo inicial no puede ser negativo o nulo.");
        return ResponseEntity.badRequest().body(respuesta);
    }

    if (dto.getFechaApertura() == null) {
        respuesta.put("status", 400);
        respuesta.put("Detalle", "La fecha de apertura no puede ser nula.");
        return ResponseEntity.badRequest().body(respuesta);
    }

    // Actualizar datos
    AperturaCaja aperturaCaja = aperturaExistente.get();
    aperturaCaja.setFechaApertura(dto.getFechaApertura());
    aperturaCaja.setFechaCierre(dto.getFechaCierre());
    aperturaCaja.setSaldoInicial(dto.getSaldoInicial());
    aperturaCaja.setSaldoFinal(dto.getSaldoFinal());
    aperturaCaja.setUsuarios(empleadoOptional.get());
    aperturaCaja.setCaja(cajaOptional.get());

    aperturaCajaService.editarAperturaCaja(aperturaCaja);

    respuesta.put("status", 200);
    respuesta.put("Detalle", "Apertura de caja actualizada correctamente.");
    return ResponseEntity.ok(respuesta);
}

@DeleteMapping("/aperturas-cajas/{id}")
public ResponseEntity<Map<String, Object>> eliminarAperturaCaja(@PathVariable Integer id) {
    Map<String, Object> respuesta = new HashMap<>();

    Optional<AperturaCaja> aperturaOpt = aperturaCajaService.buscarAperturaCaja(id);
    if (aperturaOpt.isEmpty()) {
        respuesta.put("status", 404);
        respuesta.put("Detalle", "No se encontró una apertura de caja con el ID proporcionado.");
        return ResponseEntity.status(404).body(respuesta);
    }

    aperturaCajaService.eliminarAperturaCaja(id);

    respuesta.put("status", 200);
    respuesta.put("Detalle", "La apertura de caja ha sido eliminada con éxito.");
    return ResponseEntity.ok(respuesta);
}


@PostMapping("/aperturas-cajas/{id}/cerrar")
public ResponseEntity<?>  cerrarAperturaCaja(@PathVariable Integer id) {
    Optional<AperturaCaja> aperturaOptional = aperturaCajaRepository.findById(id);
    if (aperturaOptional.isEmpty()) {
        return ResponseEntity.badRequest().body("No se encontró la apertura de caja con el ID proporcionado.");
    }

    AperturaCaja apertura = aperturaOptional.get();
        // Validar que la caja esté ABIERTO
    if (!"OCUPADA".equalsIgnoreCase(apertura.getCaja().getEstadoCaja())) {
        System.out.println(apertura.getCaja().getEstadoCaja());
        return ResponseEntity.badRequest().body("La caja no está en estado OCUPADA, no se puede cerrar.");
    }

    // Registrar fecha de cierre
    apertura.setFechaCierre(new java.util.Date());

    // Cambiar estado de la apertura a CERRADO
    apertura.setEstadoCaja("CERRADO");
    // Cambiar estado de la caja a LIBRE
    Caja caja = apertura.getCaja();
    
    caja.setEstadoCaja("LIBRE");
    caja.setSaldoActual(caja.getSaldoActual()+apertura.getSaldoFinal());
    // Guardar cambios
    aperturaCajaRepository.save(apertura);
    cajaRepository.save(caja);

    return ResponseEntity.ok(Collections.singletonMap("mensaje", "Caja cerrada correctamente"));
}

    }

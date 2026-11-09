    package com.gadbacorp.api.controller.caja;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
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

import io.swagger.v3.oas.annotations.tags.Tag;
    @Tag(name = "Caja - Apertura de caja")
    @RestController
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
        @Autowired
        private com.gadbacorp.api.repository.ventas.VentasRepository ventasRepository;

        @Autowired
        private com.gadbacorp.api.service.jpa.cajas.CierreCajaService cierreCajaService;
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

@GetMapping({"/aperturas-caja/usuario/{idUsuario}/abierta", "/aperturas-cajas/usuario/{idUsuario}/abierta"})
public ResponseEntity<?> obtenerCajaAbiertaPorUsuario(@PathVariable Integer idUsuario) {
    List<AperturaCaja> aperturas = aperturaCajaRepository
        .findByUsuarios_IdUsuarioAndEstadoCaja(idUsuario, "ABIERTA");

    if (aperturas.isEmpty()) {
        return ResponseEntity.ok().body(null); // o 404
    }

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
@org.springframework.transaction.annotation.Transactional
public ResponseEntity<Map<String, Object>> guardarAperturaCaja(@RequestBody AperturaCajaDTO dto) {
    if (dto.getId_caja() == null) {
        return error(400, "El ID de la caja no puede ser nulo.");
    }
    if (dto.getId_empleado() == null) {
        return error(400, "El ID del empleado no puede ser nulo.");
    }
    if (dto.getFechaApertura() == null) {
        return error(400, "La fecha de apertura no puede ser nula.");
    }
    if (dto.getSaldoInicial() == null || dto.getSaldoInicial().doubleValue() < 0) {
        return error(400, "El saldo inicial no puede ser nulo ni negativo.");
    }

    Usuarios empleado = empleadosRepository.findById(dto.getId_empleado()).orElse(null);
    if (empleado == null) {
        return error(400, "No se encontró el empleado con el ID proporcionado.");
    }
    Caja caja = cajaRepository.buscarBloqueando(dto.getId_caja()).orElse(null);
    if (caja == null) {
        return error(400, "No se encontró la caja con el ID proporcionado.");
    }
    com.gadbacorp.api.tenant.TenantService.exigir(com.gadbacorp.api.tenant.TenantService.empresaDe(caja.getSucursales()));

    if (empleado.getSucursal() == null || caja.getSucursales() == null
            || !empleado.getSucursal().getIdSucursal().equals(caja.getSucursales().getIdSucursal())) {
        return error(400, "La caja y el empleado deben ser de la misma sucursal.");
    }
    if (!"LIBRE".equalsIgnoreCase(caja.getEstadoCaja())) {
        return error(409, mensajeCajaNoLibre(caja));
    }
    List<AperturaCaja> abiertas = aperturaCajaRepository.findByUsuarios_IdUsuarioAndEstadoCaja(empleado.getIdUsuario(), "ABIERTA");
    if (!abiertas.isEmpty()) {
        Caja actual = abiertas.get(0).getCaja();
        return error(409, "Ya tienes una caja abierta" + (actual == null ? "" : " (" + actual.getNombreCaja() + ")")
                + ". Ciérrala antes de abrir otra.");
    }

    caja.setEstadoCaja("OCUPADA");
    cajaRepository.save(caja);

    AperturaCaja aperturaCaja = new AperturaCaja();
    aperturaCaja.setFechaApertura(dto.getFechaApertura());
    aperturaCaja.setFechaCierre(null);
    aperturaCaja.setSaldoInicial(dto.getSaldoInicial());
    aperturaCaja.setEstadoCaja("ABIERTA");
    aperturaCaja.setSaldoFinal(dto.getSaldoFinal());
    aperturaCaja.setUsuarios(empleado);
    aperturaCaja.setSaldoEfectivo(0.0);
    aperturaCaja.setCaja(caja);

    aperturaCajaService.guardarAperturaCaja(aperturaCaja);

    Map<String, Object> respuesta = new HashMap<>();
    respuesta.put("status", 200);
    respuesta.put("Detalle", "Apertura de caja registrada correctamente.");
    return ResponseEntity.ok(respuesta);
}

private String mensajeCajaNoLibre(Caja caja) {
    if ("OCUPADA".equalsIgnoreCase(caja.getEstadoCaja())) {
        String quien = aperturaCajaRepository.findByCaja_IdCajaAndEstadoCaja(caja.getIdCaja(), "ABIERTA").stream()
                .findFirst().map(a -> a.getUsuarios() == null ? null : a.getUsuarios().getUsername()).orElse(null);
        return "La " + caja.getNombreCaja() + " ya está en uso" + (quien == null ? "." : " por " + quien + ".");
    }
    if ("MALOGRADA".equalsIgnoreCase(caja.getEstadoCaja())) {
        return "La " + caja.getNombreCaja() + " está malograda y no se puede abrir.";
    }
    return "La " + caja.getNombreCaja() + " no está disponible (" + caja.getEstadoCaja() + ").";
}

private static ResponseEntity<Map<String, Object>> error(int estado, String mensaje) {
    Map<String, Object> r = new HashMap<>();
    r.put("status", estado);
    r.put("Detalle", mensaje);
    r.put("message", mensaje);
    return ResponseEntity.status(estado).body(r);
}

/** Turnos de caja abiertos en la sucursal, para elegir sobre cual cobrar. esMia indica si el turno es del usuario que consulta. */
@GetMapping("/aperturas-cajas/sucursal/{idSucursal}/abiertas")
public List<Map<String, Object>> abiertasDeLaSucursal(@PathVariable Integer idSucursal) {
    Integer yo = null;
    org.springframework.security.core.Authentication a =
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
    if (a != null && a.getPrincipal() instanceof Usuarios u) {
        yo = u.getIdUsuario();
    }
    final Integer miId = yo;
    return aperturaCajaRepository.findByCaja_Sucursales_IdSucursalAndEstadoCaja(idSucursal, "ABIERTA").stream().map(ap -> {
        Map<String, Object> f = new java.util.LinkedHashMap<>();
        f.put("idAperturaCaja", ap.getIdAperturaCaja());
        f.put("idCaja", ap.getCaja() == null ? null : ap.getCaja().getIdCaja());
        f.put("nombreCaja", ap.getCaja() == null ? null : ap.getCaja().getNombreCaja());
        f.put("idUsuario", ap.getUsuarios() == null ? null : ap.getUsuarios().getIdUsuario());
        f.put("cajero", ap.getUsuarios() == null ? null : ap.getUsuarios().getUsername());
        f.put("fechaApertura", ap.getFechaApertura());
        f.put("saldoInicial", ap.getSaldoInicial());
        f.put("saldoEfectivo", ap.getSaldoEfectivo());
        f.put("esMia", miId != null && ap.getUsuarios() != null && miId.equals(ap.getUsuarios().getIdUsuario()));
        return f;
    }).toList();
}

/** Quien cobro dentro de un turno: [{vendedor, ventas, total}]. Sirve para explicar el arqueo. */
@GetMapping("/aperturas-cajas/{idApertura}/vendedores")
public List<Map<String, Object>> vendedoresDelTurno(@PathVariable Integer idApertura) {
    return ventasRepository.resumenPorVendedor(idApertura).stream().map(f -> {
        Map<String, Object> r = new java.util.LinkedHashMap<>();
        r.put("vendedor", f[0]);
        r.put("ventas", f[1]);
        r.put("total", f[2]);
        return r;
    }).toList();
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
public ResponseEntity<?> cerrarAperturaCaja(@PathVariable Integer id) {
    try {
        cierreCajaService.cerrar(id);
        return ResponseEntity.ok(Collections.singletonMap("mensaje", "Caja cerrada correctamente"));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    }

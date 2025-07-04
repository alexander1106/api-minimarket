    package com.gadbacorp.api.controller.caja;

import java.util.Collections;
    import java.util.List;
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
    import com.gadbacorp.api.entity.empleados.Usuarios;
    import com.gadbacorp.api.repository.caja.AperturaCajaRepository;
    import com.gadbacorp.api.repository.caja.CajaRepository;
    import com.gadbacorp.api.repository.empleados.UsuarioRepository;
    import com.gadbacorp.api.service.caja.IAperturaCajaService;

    @RestController
    @CrossOrigin("*")    
    @RequestMapping("/api/minimarket")
    public class AperturaCajaController {

        @Autowired
        private IAperturaCajaService aperturaCajaService;

        @Autowired
        private UsuarioRepository empleadosRepository;

    
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

        
     @GetMapping("/aperturas-cajas/{idAperturaCaja}/caja")
    public ResponseEntity<Caja> obtenerCajaPorApertura(@PathVariable Integer idAperturaCaja) {
        return aperturaCajaRepository.findById(idAperturaCaja)
                .map(apertura -> ResponseEntity.ok(apertura.getCaja()))
                .orElse(ResponseEntity.notFound().build());
    }

     @PostMapping("/aperturas-cajas")
public ResponseEntity<?> guardarAperturaCaja(@RequestBody AperturaCajaDTO dto) {
    // Validaciones previas
    if (dto.getId_caja() == null) {
        return ResponseEntity.badRequest().body("El ID de la caja no puede ser nulo.");
    }
    if (dto.getId_empleado() == null) {
        return ResponseEntity.badRequest().body("El ID del empleado no puede ser nulo.");
    }
    if (dto.getFechaApertura() == null) {
        return ResponseEntity.badRequest().body("La fecha de apertura no puede ser nula.");
    }
    if (dto.getSaldoInicial() == null || dto.getSaldoInicial().doubleValue() < 0) {
        return ResponseEntity.badRequest().body("El saldo inicial no puede ser nulo ni negativo.");
    }

    // Buscar empleado y caja
    Optional<Usuarios> empleadoOptional = empleadosRepository.findById(dto.getId_empleado());
    if (empleadoOptional.isEmpty()) {
        return ResponseEntity.badRequest().body("No se encontró el empleado con el ID proporcionado.");
    }

    Optional<Caja> cajaOptional = cajaRepository.findById(dto.getId_caja());
    if (cajaOptional.isEmpty()) {
        return ResponseEntity.badRequest().body("No se encontró la caja con el ID proporcionado.");
    }

    Caja caja = cajaOptional.get();

    // Cambiar estado de la caja a "abierto"
    caja.setEstadoCaja("OCUPADA");  // Ajusta el nombre del campo y valor según tu modelo
    cajaRepository.save(caja);   // Guardar el cambio en la base de datos

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

    return ResponseEntity.ok(aperturaCajaService.guardarAperturaCaja(aperturaCaja));
}


@PutMapping("/aperturas-cajas")
public ResponseEntity<?> modificarAperturaCaja(@RequestBody AperturaCajaDTO dto) {
    // Validar campos nulos
    if(dto.getIdAperturaCaja() == null){
        return ResponseEntity.badRequest().body("El ID de la apertura de caja no puede ser nulo.");
    }
    if(dto.getId_caja() == null){
        return ResponseEntity.badRequest().body("El ID de la caja no puede ser nulo.");
    }
    if(dto.getId_empleado() == null){
        return ResponseEntity.badRequest().body("El ID del empleado no puede ser nulo.");
    }

    // Validar existencia en la base de datos
    Optional<AperturaCaja> aperturaExistente = aperturaCajaService.buscarAperturaCaja(dto.getIdAperturaCaja());
    if(aperturaExistente.isEmpty()) {
        return ResponseEntity.badRequest().body("No se encontró una apertura de caja con el ID proporcionado.");
    }

    Optional<Caja> cajaOptional = cajaRepository.findById(dto.getId_caja());
    if(cajaOptional.isEmpty()) {
        return ResponseEntity.badRequest().body("No se encontró la caja con el ID proporcionado.");
    }

    Optional<Usuarios> empleadoOptional = empleadosRepository.findById(dto.getId_empleado());
    if(empleadoOptional.isEmpty()) {
        return ResponseEntity.badRequest().body("No se encontró el empleado con el ID proporcionado.");
    }

    // Validar datos financieros y fechas
    if(dto.getSaldoInicial() == null || dto.getSaldoInicial().doubleValue() < 0){
        return ResponseEntity.badRequest().body("El saldo inicial no puede ser negativo o nulo.");
    }

    if(dto.getFechaApertura() == null){
        return ResponseEntity.badRequest().body("La fecha de apertura no puede ser nula.");
    }

    // Crear objeto con datos actualizados
    AperturaCaja aperturaCaja = aperturaExistente.get();
    aperturaCaja.setFechaApertura(dto.getFechaApertura());
    aperturaCaja.setFechaCierre(dto.getFechaCierre());
    aperturaCaja.setSaldoInicial(dto.getSaldoInicial());
    aperturaCaja.setSaldoFinal(dto.getSaldoFinal());
    aperturaCaja.setUsuarios(empleadoOptional.get());
    aperturaCaja.setCaja(cajaOptional.get());

    return ResponseEntity.ok(aperturaCajaService.editarAperturaCaja(aperturaCaja));
}

        @DeleteMapping("/aperturas-cajas/{id}")
        public String eliminarAperturaCaja(@PathVariable Integer id){
            aperturaCajaService.eliminarAperturaCaja(id);
            return "La apertura de caja  a sido eliminad con exito";    
        }


@PostMapping("/aperturas-cajas/{id}/cerrar")
public ResponseEntity<?>  cerrarAperturaCaja(@PathVariable Integer id) {
    // Buscar la apertura
    Optional<AperturaCaja> aperturaOptional = aperturaCajaRepository.findById(id);
    if (aperturaOptional.isEmpty()) {
        return ResponseEntity.badRequest().body("No se encontró la apertura de caja con el ID proporcionado.");
    }

    AperturaCaja apertura = aperturaOptional.get();

    // Validar que la caja esté ABIERTO
if (!"OCUPADA".equalsIgnoreCase(apertura.getCaja().getEstadoCaja())) {
    return ResponseEntity.badRequest().body("La caja no está en estado OCUPADA, no se puede cerrar.");
}


    // Registrar fecha de cierre
    apertura.setFechaCierre(new java.util.Date());

    // Cambiar estado de la apertura a CERRADO
    apertura.setEstadoCaja("CERRADO");

    // Cambiar estado de la caja a LIBRE
    Caja caja = apertura.getCaja();
    caja.setEstadoCaja("LIBRE");

    // Guardar cambios
    aperturaCajaRepository.save(apertura);
    cajaRepository.save(caja);

    return ResponseEntity.ok(Collections.singletonMap("mensaje", "Caja cerrada correctamente"));
}

    }

package com.gadbacorp.api.controller.administrable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gadbacorp.api.entity.administrable.EmpresaResumenDTO;
import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.entity.administrable.RegistroEmpresaRequest;
import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.compras.Proveedores;
import com.gadbacorp.api.repository.administrable.SucursalesRepository;
import com.gadbacorp.api.repository.compras.ProveedoresRepository;
import com.gadbacorp.api.service.administrable.IEmpresasService;

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Administración - Empresas")
@RestController
@RequestMapping("/api/minimarket/empresas")
public class EmpresasController {

    @Autowired
    private IEmpresasService serviceEmpresas;
    
    @Value("${upload.dir}") 
    private String uploadDir;

    @Autowired
    private SucursalesRepository sucursalesRepository;

    @Autowired
    private com.gadbacorp.api.service.jpa.administrable.EmpresaResumenService empresaResumenService;

    @Autowired
    private com.gadbacorp.api.service.jpa.administrable.EmpresaRegistroService empresaRegistroService;

    @Autowired
    private com.gadbacorp.api.service.jpa.administrable.VerificacionSunatService verificacionSunatService;

    @Autowired
    private com.gadbacorp.api.repository.administrable.PlanRepository planRepository;

    @Autowired
    private com.gadbacorp.api.repository.empleados.UsuarioRepository usuarioRepository;

        @Autowired
    private ProveedoresRepository proveedoresRepository;

    @GetMapping("/{idEmpresa}/proveedores")
    public ResponseEntity<List<Proveedores>> obtenerProveedoresPorEmpresa(@PathVariable Integer idEmpresa) {
        Optional<Empresas> empresaOpt = serviceEmpresas.buscarId(idEmpresa);
        if (empresaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Empresas empresa = empresaOpt.get();
        List<Proveedores> proveedores = empresa.getProveedores(); // Aquí obtenemos la lista de proveedores de la empresa

        if (proveedores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(proveedores);
    }

    /** Panel de suscripciones: datos de la empresa + plan, ingreso mensual, vencimiento, usuarios y estado. */
    @GetMapping
    public List<EmpresaResumenDTO> buscarTodos() {
        return empresaResumenService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResumenDTO> buscarId(@PathVariable Integer id) {
        return empresaResumenService.buscar(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Usuarios de todas las sucursales de la empresa (para el Superadministrador). */
    @GetMapping("/{id}/usuarios")
    public ResponseEntity<?> usuariosDeLaEmpresa(@PathVariable Integer id) {
        if (serviceEmpresas.buscarId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuarioRepository.findBySucursal_Empresa_Idempresa(id));
    }

    /** Suspende la empresa: sigue existiendo pero queda con estado SUSPENDIDO y sin ingreso. */
    @PostMapping("/{id}/suspender")
    public ResponseEntity<?> suspender(@PathVariable Integer id) {
        return cambiarSuspension(id, true);
    }

    @PostMapping("/{id}/reactivar")
    public ResponseEntity<?> reactivar(@PathVariable Integer id) {
        return cambiarSuspension(id, false);
    }

    private ResponseEntity<?> cambiarSuspension(Integer id, boolean suspender) {
        Optional<Empresas> opt = serviceEmpresas.buscarId(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Empresas e = opt.get();
        e.setSuspendida(suspender);
        serviceEmpresas.modificar(e);
        return empresaResumenService.buscar(id).<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Cuerpo de PUT /empresas/{id}/suscripcion (todos los campos son opcionales). */
    public record SuscripcionRequest(Integer idPlan, String ciclo, String fechaVencimiento, Boolean enPrueba) {
    }

    /** Cambio de plan, renovacion (nuevo vencimiento) o paso de prueba a pago, sin tocar el resto de datos. */
    @PutMapping("/{id}/suscripcion")
    public ResponseEntity<?> actualizarSuscripcion(@PathVariable Integer id, @RequestBody SuscripcionRequest req) {
        Optional<Empresas> opt = serviceEmpresas.buscarId(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Empresas e = opt.get();
        try {
            aplicarSuscripcion(e, null, null, null, req.idPlan(), req.ciclo(), req.fechaVencimiento(), req.enPrueba());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", ex.getMessage()));
        }
        serviceEmpresas.modificar(e);
        return empresaResumenService.buscar(id).<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Consulta el RUC en SUNAT y marca la empresa como verificada si existe y la razon social coincide. */
    @PostMapping("/{id}/verificar-sunat")
    public ResponseEntity<?> verificarSunat(@PathVariable Integer id) {
        Optional<Empresas> opt = serviceEmpresas.buscarId(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (!verificacionSunatService.estaConfigurado()) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("message", "La verificacion SUNAT no esta configurada (APIS_TOKEN_RUC)."));
        }
        Empresas e = opt.get();
        try {
            e.setSunatVerificado(verificacionSunatService.verificar(e.getRuc(), e.getRazonsocial()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(java.util.Map.of("message", ex.getMessage()));
        }
        serviceEmpresas.modificar(e);
        return empresaResumenService.buscar(id).<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Datos de suscripcion que llegan opcionalmente en el formulario; lo que no llega no se modifica. */
    private void aplicarSuscripcion(Empresas e, String propietario, String telefono, String distrito,
                                    Integer idPlan, String ciclo, String fechaVencimiento, Boolean enPrueba) {
        if (propietario != null) e.setPropietario(propietario);
        if (telefono != null) e.setTelefono(telefono);
        if (distrito != null) e.setDistrito(distrito);
        if (idPlan != null) {
            e.setPlan(planRepository.findById(idPlan)
                    .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado con ID: " + idPlan)));
        }
        if (ciclo != null && !ciclo.isBlank()) {
            String c = ciclo.trim().toUpperCase();
            if (!c.equals("MENSUAL") && !c.equals("ANUAL")) {
                throw new IllegalArgumentException("El ciclo debe ser MENSUAL o ANUAL.");
            }
            e.setCiclo(c);
        }
        if (fechaVencimiento != null && !fechaVencimiento.isBlank()) {
            try {
                e.setFechaVencimiento(LocalDate.parse(fechaVencimiento));
            } catch (java.time.format.DateTimeParseException ex) {
                throw new IllegalArgumentException("La fecha de vencimiento debe tener el formato AAAA-MM-DD.");
            }
        }
        if (enPrueba != null) e.setEnPrueba(enPrueba);
    }

    /** Alta desde el formulario "Registrar Nueva Empresa": crea la empresa y sus sucursales en una transaccion. */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmpresaResumenDTO> registrar(@RequestBody RegistroEmpresaRequest req) {
        Integer id = empresaRegistroService.registrar(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(empresaResumenService.buscar(id).orElseThrow());
    }

    /** Edicion por JSON: solo se actualizan los campos que llegan. */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmpresaResumenDTO> editar(@PathVariable Integer id, @RequestBody RegistroEmpresaRequest req) {
        empresaRegistroService.actualizar(id, req);
        return empresaResumenService.buscar(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> guardar(
        @RequestParam String razonsocial,
        @RequestParam String ciudad,
        @RequestParam String direccion,
        @RequestParam String ruc,
        @RequestParam String correo,
        @RequestParam(defaultValue = "0") Integer cant_sucursales,
        @RequestParam(defaultValue = "0") Integer cant_cajas,
        @RequestParam(defaultValue = "0") Integer cant_trabajadores,
        @RequestParam(defaultValue = "0") Integer limit_inventario,
        @RequestParam String fechaRegistro,
        @RequestParam Integer estado,
        @RequestParam(required = false) MultipartFile logo,
        @RequestParam(required = false) String propietario,
        @RequestParam(required = false) String telefono,
        @RequestParam(required = false) String distrito,
        @RequestParam(required = false) Integer idPlan,
        @RequestParam(required = false) String ciclo,
        @RequestParam(required = false) String fechaVencimiento,
        @RequestParam(required = false) Boolean enPrueba
    ) throws IOException {

        Empresas e = new Empresas();
        e.setRazonsocial(razonsocial);
        e.setCiudad(ciudad);
        e.setDireccion(direccion);
        e.setRuc(ruc);
        e.setCorreo(correo);
     
        e.setFechaRegistro(LocalDate.parse(fechaRegistro));
        e.setEstado(estado);
        try {
            aplicarSuscripcion(e, propietario, telefono, distrito, idPlan, ciclo, fechaVencimiento, enPrueba);
        } catch (IllegalArgumentException ex) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }

        if (logo != null && !logo.isEmpty()) {
            String fileName = "empresa_" + System.currentTimeMillis() + "_" + logo.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            Path targetPath = uploadPath.resolve(fileName);
            logo.transferTo(targetPath.toFile());
            e.setLogo(fileName);
        } else {
            e.setLogo(null);
        }

        serviceEmpresas.guardar(e);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Empresas> modificar(
        @RequestParam Integer idempresa,
        @RequestParam String razonsocial,
        @RequestParam String ciudad,
        @RequestParam String direccion,
        @RequestParam String ruc,
        @RequestParam String correo,
        @RequestParam(required = false, defaultValue = "0") Integer cant_sucursales,
        @RequestParam(required = false, defaultValue = "0") Integer cant_cajas,
        @RequestParam(required = false, defaultValue = "0") Integer cant_trabajadores,
        @RequestParam(required = false, defaultValue = "0") Integer limit_inventario,
        @RequestParam String fechaRegistro,
        @RequestParam Integer estado,
        @RequestParam(required = false) MultipartFile logo,
        @RequestParam(required = false) String propietario,
        @RequestParam(required = false) String telefono,
        @RequestParam(required = false) String distrito,
        @RequestParam(required = false) Integer idPlan,
        @RequestParam(required = false) String ciclo,
        @RequestParam(required = false) String fechaVencimiento,
        @RequestParam(required = false) Boolean enPrueba
    ) throws Exception {
        Empresas e = serviceEmpresas.buscarId(idempresa)
                         .orElse(new Empresas());
        e.setIdempresa(idempresa);
        e.setRazonsocial(razonsocial);
        e.setCiudad(ciudad);
        e.setDireccion(direccion);
        e.setRuc(ruc);
        e.setCorreo(correo);
       
        e.setFechaRegistro(LocalDate.parse(fechaRegistro));
        e.setEstado(estado);
        try {
            aplicarSuscripcion(e, propietario, telefono, distrito, idPlan, ciclo, fechaVencimiento, enPrueba);
        } catch (IllegalArgumentException ex) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }

        if (logo != null && !logo.isEmpty()) {
            String fileName = "empresa_" + System.currentTimeMillis() + "_" + logo.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            Path targetPath = uploadPath.resolve(fileName);
            logo.transferTo(targetPath.toFile());
            e.setLogo(fileName);
        }

        serviceEmpresas.modificar(e);

        if (e.getLogo() != null) {
            Path imgPath = Paths.get(uploadDir).resolve(e.getLogo()).toAbsolutePath().normalize();
            if (Files.exists(imgPath)) {
                byte[] bytes = Files.readAllBytes(imgPath);
                String base64 = Base64.getEncoder().encodeToString(bytes);
                e.setLogo(base64);
            } else {
                e.setLogo(null);
            }
        }

        return ResponseEntity.ok(e);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id){
        serviceEmpresas.eliminar(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{idEmpresa}/sucursales")
    public ResponseEntity<List<Sucursales>> listarSucursalesPorEmpresa(@PathVariable Integer idEmpresa) {
        List<Sucursales> sucursales = sucursalesRepository.findByEmpresaIdempresa(idEmpresa);
        if (sucursales.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(sucursales);
    }
}

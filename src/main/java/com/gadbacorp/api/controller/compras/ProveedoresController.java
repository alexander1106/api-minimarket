package com.gadbacorp.api.controller.compras;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.entity.compras.ProveedorDTO;
import com.gadbacorp.api.entity.compras.Proveedores;
import com.gadbacorp.api.service.compras.IProveedoresService;

import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;
@Tag(name = "Compras - Proveedores")
@RestController
@RequestMapping("/api/minimarket/proveedores")
public class ProveedoresController {

    @Autowired
    private IProveedoresService serviceProveedores;

    @Autowired
    private com.gadbacorp.api.service.jpa.compras.ProveedorRegistroService proveedorRegistroService;

    @Autowired
    private com.gadbacorp.api.repository.compras.ComprasRepository comprasRepository;

    @Autowired
    private com.gadbacorp.api.service.jpa.compras.ApiService apiService;

    @Autowired
    private com.gadbacorp.api.service.jpa.administrable.VerificacionSunatService verificacionSunatService;

    @GetMapping
    public List<ProveedorDTO> buscarTodos() {
        return aDtos(serviceProveedores.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDTO> buscarPorId(@PathVariable Integer id) {
        Optional<Proveedores> opt = serviceProveedores.buscarId(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(opt.get()));
    }

    /** Proveedores de una empresa. Si no tiene ninguno responde 200 con una lista vacia. */
    @GetMapping("/empresas/{idEmpresa}")
    public List<ProveedorDTO> buscarPorEmpresa(@PathVariable Integer idEmpresa) {
        return aDtos(serviceProveedores.buscarPorEmpresa(idEmpresa));
    }

    /** Autocompleta el formulario con los datos de SUNAT para un RUC (razon social, direccion, estado...). */
    @GetMapping(value = "/sunat/{ruc}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> consultarSunat(@PathVariable String ruc) {
        if (!ruc.matches("\\d{11}")) {
            return ResponseEntity.badRequest().body("{\"message\":\"El RUC debe tener 11 dígitos.\"}");
        }
        // Se espera la respuesta dentro de la misma petición: devolver un Mono desde MVC provoca un
        // segundo despacho asíncrono sin sesión y Spring Security responde 403.
        try {
            String cuerpo = apiService.consumirApi(ruc).block(java.time.Duration.ofSeconds(15));
            return ResponseEntity.ok(cuerpo == null ? "{}" : cuerpo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("{\"message\":\"No se pudo consultar a SUNAT.\"}");
        }
    }

    @GetMapping("/{idProveedor}/empresas")
    public ResponseEntity<?> obtenerEmpresaPorProveedor(@PathVariable Integer idProveedor) {
        Optional<Proveedores> proveedorOpt = serviceProveedores.buscarId(idProveedor);
        if (proveedorOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Empresas empresa = proveedorOpt.get().getEmpresa();
        if (empresa == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(empresa.getRazonsocial());
    }

    @PostMapping
    public ResponseEntity<ProveedorDTO> guardar(@RequestBody ProveedorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(proveedorRegistroService.crear(dto)));
    }

    @PutMapping
    public ProveedorDTO actualizar(@RequestBody ProveedorDTO dto) {
        return toDto(proveedorRegistroService.actualizar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        proveedorRegistroService.eliminar(id);
        return ResponseEntity.ok("Proveedor eliminado correctamente.");
    }

    /** Convierte una lista calculando el historial de compras de todos con una sola consulta. */
    private List<ProveedorDTO> aDtos(List<Proveedores> lista) {
        Map<Integer, Object[]> resumen = resumenCompras();
        return lista.stream().map(p -> toDto(p, resumen)).collect(Collectors.toList());
    }

    private ProveedorDTO toDto(Proveedores p) {
        return toDto(p, resumenCompras());
    }

    private Map<Integer, Object[]> resumenCompras() {
        Map<Integer, Object[]> mapa = new HashMap<>();
        for (Object[] fila : comprasRepository.resumenPorProveedor()) {
            mapa.put(((Number) fila[0]).intValue(), fila);
        }
        return mapa;
    }

    private ProveedorDTO toDto(Proveedores p, Map<Integer, Object[]> resumen) {
        ProveedorDTO dto = new ProveedorDTO();
        dto.setIdProveedor(p.getIdProveedor());
        dto.setNombre(p.getNombre());
        dto.setRuc(p.getRuc());
        dto.setRegimen(p.getRegimen());
        dto.setTelefono(p.getTelefono());
        dto.setGmail(p.getGmail());
        dto.setDireccion(p.getDireccion());
        dto.setFecha_registro(p.getFecha_registro());
        dto.setEstado(p.getEstado());
        dto.setIdEmpresa(p.getEmpresa() != null ? p.getEmpresa().getIdempresa() : null);
        dto.setEmpresaNombre(p.getEmpresa() != null ? p.getEmpresa().getRazonsocial() : null);

        dto.setRazonSocial(p.getRazonSocial());
        dto.setDistrito(p.getDistrito());
        dto.setProvincia(p.getProvincia());
        dto.setDepartamento(p.getDepartamento());
        dto.setContactoNombre(p.getContactoNombre());
        dto.setContactoCargo(p.getContactoCargo());
        dto.setTelefonoSecundario(p.getTelefonoSecundario());
        dto.setSitioWeb(p.getSitioWeb());
        dto.setRubro(p.getRubro());
        dto.setCondicionPago(p.getCondicionPago() != null ? p.getCondicionPago() : "CONTADO");
        dto.setDiasCredito(p.getDiasCredito());
        dto.setMoneda(p.getMoneda() != null ? p.getMoneda() : "PEN");
        dto.setLimiteCredito(p.getLimiteCredito());
        dto.setBanco(p.getBanco());
        dto.setNumeroCuenta(p.getNumeroCuenta());
        dto.setCci(p.getCci());
        dto.setEstadoSunat(p.getEstadoSunat());
        dto.setCondicionSunat(p.getCondicionSunat());
        dto.setNotas(p.getNotas());

        String ruc = p.getRuc();
        dto.setTipoPersona(ruc != null && ruc.startsWith("20") ? "JURIDICA" : "NATURAL");
        Object[] fila = resumen.get(p.getIdProveedor());
        if (fila != null) {
            dto.setTotalCompras(((Number) fila[1]).longValue());
            dto.setMontoTotalComprado(new java.math.BigDecimal(fila[2].toString()));
            Object ultima = fila[3];
            dto.setUltimaCompra(ultima == null ? null
                    : ((java.time.LocalDateTime) ultima).toLocalDate().toString());
        }
        return dto;
    }
}

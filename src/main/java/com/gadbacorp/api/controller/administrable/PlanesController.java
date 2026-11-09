package com.gadbacorp.api.controller.administrable;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.administrable.Plan;
import com.gadbacorp.api.excepciones.OperacionNoPermitidaException;
import com.gadbacorp.api.excepciones.RecursoDuplicadoException;
import com.gadbacorp.api.excepciones.RegistroInvalidoException;
import com.gadbacorp.api.repository.administrable.EmpresasRepository;
import com.gadbacorp.api.repository.administrable.PlanRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

/** Planes de suscripcion de la plataforma. Los consulta cualquier usuario autenticado; solo el SUPERADMIN los modifica. */
@Tag(name = "Administración - Planes")
@RestController
@RequestMapping("/api/minimarket/planes")
public class PlanesController {

    private final PlanRepository planRepository;
    private final EmpresasRepository empresasRepository;

    public PlanesController(PlanRepository planRepository, EmpresasRepository empresasRepository) {
        this.planRepository = planRepository;
        this.empresasRepository = empresasRepository;
    }

    /** Plan con la cantidad de empresas que lo usan, para mostrarlo en el panel. */
    public record PlanDTO(Integer idPlan, String nombre, BigDecimal precioMensual, Integer maxUsuarios,
                          Integer maxSucursales, Integer estado, long totalEmpresas) {
    }

    private PlanDTO aDto(Plan p) {
        return new PlanDTO(p.getIdPlan(), p.getNombre(), p.getPrecioMensual(), p.getMaxUsuarios(), p.getMaxSucursales(),
                p.getEstado(), empresasRepository.countByPlan_IdPlan(p.getIdPlan()));
    }

    @GetMapping
    public List<PlanDTO> listar(@RequestParam(defaultValue = "false") boolean soloActivos) {
        return planRepository.findAll().stream()
                .filter(p -> !soloActivos || Integer.valueOf(1).equals(p.getEstado()))
                .map(this::aDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanDTO> buscar(@PathVariable Integer id) {
        return planRepository.findById(id).map(p -> ResponseEntity.ok(aDto(p))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<PlanDTO> crear(@RequestBody Plan cuerpo) {
        validar(cuerpo);
        planRepository.findByNombreIgnoreCase(cuerpo.getNombre().trim()).ifPresent(otro -> {
            throw new RecursoDuplicadoException("Ya existe un plan con ese nombre.");
        });
        Plan nuevo = new Plan();
        copiar(cuerpo, nuevo);
        nuevo.setEstado(1);
        return ResponseEntity.status(HttpStatus.CREATED).body(aDto(planRepository.save(nuevo)));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<PlanDTO> editar(@RequestBody Plan cuerpo) {
        if (cuerpo.getIdPlan() == null) {
            throw new RegistroInvalidoException(Map.of("idPlan", "El id del plan es obligatorio"));
        }
        Plan existente = planRepository.findById(cuerpo.getIdPlan()).orElse(null);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        validar(cuerpo);
        planRepository.findByNombreIgnoreCase(cuerpo.getNombre().trim()).ifPresent(otro -> {
            if (!otro.getIdPlan().equals(existente.getIdPlan())) {
                throw new RecursoDuplicadoException("Ya existe un plan con ese nombre.");
            }
        });
        copiar(cuerpo, existente);
        if (cuerpo.getEstado() != null) {
            existente.setEstado(cuerpo.getEstado() == 0 ? 0 : 1);
        }
        return ResponseEntity.ok(aDto(planRepository.save(existente)));
    }

    /** Desactiva el plan (no se borra): no se ofrece a empresas nuevas. Si hay empresas que lo usan responde 409. */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, String>> desactivar(@PathVariable Integer id) {
        Plan plan = planRepository.findById(id).orElse(null);
        if (plan == null) {
            return ResponseEntity.notFound().build();
        }
        long usan = empresasRepository.countByPlan_IdPlan(id);
        if (usan > 0) {
            throw new OperacionNoPermitidaException(
                    "No se puede desactivar: " + usan + (usan == 1 ? " empresa usa" : " empresas usan") + " este plan. Cámbieles el plan primero.");
        }
        plan.setEstado(0);
        planRepository.save(plan);
        return ResponseEntity.ok(Map.of("message", "Plan desactivado"));
    }

    private static void copiar(Plan origen, Plan destino) {
        destino.setNombre(origen.getNombre().trim());
        destino.setPrecioMensual(origen.getPrecioMensual());
        destino.setMaxUsuarios(origen.getMaxUsuarios());
        destino.setMaxSucursales(origen.getMaxSucursales());
    }

    private static void validar(Plan plan) {
        Map<String, String> errores = new java.util.LinkedHashMap<>();
        if (plan.getNombre() == null || plan.getNombre().isBlank()) {
            errores.put("nombre", "El nombre del plan es obligatorio");
        }
        if (plan.getPrecioMensual() == null || plan.getPrecioMensual().signum() < 0) {
            errores.put("precioMensual", "El precio mensual no puede ser negativo");
        }
        if (plan.getMaxUsuarios() != null && plan.getMaxUsuarios() < 1) {
            errores.put("maxUsuarios", "Debe ser al menos 1 (o vacío para no limitar)");
        }
        if (plan.getMaxSucursales() != null && plan.getMaxSucursales() < 1) {
            errores.put("maxSucursales", "Debe ser al menos 1 (o vacío para no limitar)");
        }
        if (!errores.isEmpty()) {
            throw new RegistroInvalidoException(errores);
        }
    }
}

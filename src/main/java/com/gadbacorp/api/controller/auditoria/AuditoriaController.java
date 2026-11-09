package com.gadbacorp.api.controller.auditoria;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.service.jpa.auditoria.AuditoriaService;

import io.swagger.v3.oas.annotations.tags.Tag;

/** Auditoria de la plataforma: solo el SUPERADMIN. Solo lectura. */
@Tag(name = "Auditoría")
@RestController
@RequestMapping("/api/minimarket/auditoria")
public class AuditoriaController {

    private final AuditoriaService service;

    public AuditoriaController(AuditoriaService service) {
        this.service = service;
    }

    /**
     * Lo mas reciente primero. Sin fechas trae los ultimos 30 dias.
     * Respuesta: {items, total, pagina, totalPaginas}.
     */
    @GetMapping
    public Map<String, Object> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer idEmpresa,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) String modulo,
            @RequestParam(required = false) Boolean exito,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "25") int tamano) {
        return service.buscar(desde, hasta, username, idEmpresa, accion, modulo, exito, q, pagina, tamano);
    }

    /** Conteos del periodo para las tarjetas: total, fallidos, por acción, por módulo y los 10 usuarios más activos. */
    @GetMapping("/resumen")
    public Map<String, Object> resumen(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return service.resumen(desde, hasta);
    }

    /** Valores para llenar los selectores de filtros. */
    @GetMapping("/filtros")
    public Map<String, Object> filtros() {
        return service.filtros();
    }

    /** Un registro completo: IP, navegador, duración y los datos que envió el usuario. */
    @GetMapping("/{id}")
    public Map<String, Object> detalle(@PathVariable Long id) {
        return service.detalle(id);
    }
}

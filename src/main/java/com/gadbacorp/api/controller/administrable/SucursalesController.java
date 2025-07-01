package com.gadbacorp.api.controller.administrable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.entity.administrable.SucursalDTO;
import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.repository.administrable.EmpresasRepository;
import com.gadbacorp.api.service.administrable.ISucursalesService;

@RestController
@RequestMapping("/api/minimarket/sucursales")
@CrossOrigin(origins = "http://localhost:4200")
public class SucursalesController {

    @Autowired
    private ISucursalesService sucursalesService;

    @Autowired
    private EmpresasRepository empresasRepository;

    /** Listado mapeado a DTO */
    @GetMapping
    public List<SucursalDTO> buscarTodos() {
        return sucursalesService.buscarTodos().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    /** Busqueda por id mapeada a DTO */
    @GetMapping("/{id}")
    public ResponseEntity<SucursalDTO> buscarPorId(@PathVariable Integer id) {
        Optional<Sucursales> opt = sucursalesService.buscarId(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(opt.get()));
    }

    /** Crear */
    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody SucursalDTO dto) {
        // validar empresa
        Optional<Empresas> empOpt = empresasRepository.findById(dto.getIdEmpresa());
        if (empOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                .body("Empresa no encontrada con ID: " + dto.getIdEmpresa());
        }
        // mapear y guardar
        Sucursales s = new Sucursales();
        s.setNombreSucursal(dto.getNombreSucursal());
        s.setContacto(dto.getContacto());
        s.setDireccion(dto.getDireccion());
        s.setEstado(dto.getEstado());
        s.setEmpresa(empOpt.get());
        Sucursales saved = sucursalesService.guardar(s);
        return ResponseEntity.ok(toDto(saved));
    }

    /** Actualizar */
    @PutMapping
    public ResponseEntity<?> actualizar(@RequestBody SucursalDTO dto) {
        if (dto.getIdSucursal() == null) {
            return ResponseEntity.badRequest().body("IdSucursal es obligatorio");
        }
        Optional<Empresas> empOpt = empresasRepository.findById(dto.getIdEmpresa());
        if (empOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                .body("Empresa no encontrada con ID: " + dto.getIdEmpresa());
        }
        Sucursales s = new Sucursales();
        s.setIdSucursal(dto.getIdSucursal());
        s.setNombreSucursal(dto.getNombreSucursal());
        s.setContacto(dto.getContacto());
        s.setDireccion(dto.getDireccion());
        s.setEstado(dto.getEstado());
        s.setEmpresa(empOpt.get());
        Sucursales updated = sucursalesService.guardar(s);
        return ResponseEntity.ok(toDto(updated));
    }

    /** Eliminar lógico */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        sucursalesService.eliminar(id);
        return ResponseEntity.ok("Sucursal eliminada correctamente.");
    }

    /** Utilitario para mapear entidad → DTO */
    private SucursalDTO toDto(Sucursales s) {
        SucursalDTO dto = new SucursalDTO();
        dto.setIdSucursal(s.getIdSucursal());
        dto.setNombreSucursal(s.getNombreSucursal());
        dto.setContacto(s.getContacto());
        dto.setDireccion(s.getDireccion());
        dto.setEstado(s.getEstado());
        dto.setIdEmpresa(s.getEmpresa().getIdempresa());
        dto.setEmpresaNombre(s.getEmpresa().getRazonsocial());
        return dto;
    }
}

package com.gadbacorp.api.controller.compras;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gadbacorp.api.entity.compras.Proveedores;
import com.gadbacorp.api.entity.compras.ProveedorDTO;
import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.repository.administrable.EmpresasRepository;
import com.gadbacorp.api.service.compras.IProveedoresService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/minimarket/proveedores")
public class ProveedoresController {

    @Autowired
    private IProveedoresService serviceProveedores;

    @Autowired
    private EmpresasRepository empresasRepository;

    // ✅ Listar todos los proveedores
    @GetMapping
    public List<ProveedorDTO> buscarTodos() {
        return serviceProveedores.buscarTodos().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    // ✅ Obtener proveedor por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDTO> buscarPorId(@PathVariable Integer id) {
        Optional<Proveedores> opt = serviceProveedores.buscarId(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(opt.get()));
    }

    // ✅ Buscar proveedores por empresa
    @GetMapping("/empresas/{idEmpresa}")
    public ResponseEntity<List<ProveedorDTO>> buscarPorEmpresa(@PathVariable Integer idEmpresa) {
        List<Proveedores> proveedores = serviceProveedores.buscarPorEmpresa(idEmpresa);
        if (proveedores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<ProveedorDTO> dtos = proveedores.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ✅ Obtener la empresa (nombre) de un proveedor específico
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

        // Solo devolver el nombre de la empresa
        return ResponseEntity.ok(empresa.getRazonsocial());
    }

    // ✅ Guardar proveedor
    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody ProveedorDTO dto) {
        Optional<Empresas> empOpt = empresasRepository.findById(dto.getIdEmpresa());
        if (empOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Empresa no encontrada con ID: " + dto.getIdEmpresa());
        }

        Proveedores p = new Proveedores();
        p.setNombre(dto.getNombre());
        p.setRuc(dto.getRuc());
        p.setRegimen(dto.getRegimen());
        p.setTelefono(dto.getTelefono());
        p.setGmail(dto.getGmail());
        p.setDireccion(dto.getDireccion());
        p.setFecha_registro(dto.getFecha_registro());
        p.setEstado(dto.getEstado());
        p.setEmpresa(empOpt.get());

        Proveedores saved = serviceProveedores.guardar(p);
        return ResponseEntity.ok(toDto(saved));
    }

    // ✅ Actualizar proveedor
    @PutMapping
    public ResponseEntity<?> actualizar(@RequestBody ProveedorDTO dto) {
        if (dto.getIdProveedor() == null) {
            return ResponseEntity.badRequest().body("IdProveedor es obligatorio");
        }

        Optional<Empresas> empOpt = empresasRepository.findById(dto.getIdEmpresa());
        if (empOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Empresa no encontrada con ID: " + dto.getIdEmpresa());
        }

        Proveedores p = new Proveedores();
        p.setIdProveedor(dto.getIdProveedor());
        p.setNombre(dto.getNombre());
        p.setRuc(dto.getRuc());
        p.setRegimen(dto.getRegimen());
        p.setTelefono(dto.getTelefono());
        p.setGmail(dto.getGmail());
        p.setDireccion(dto.getDireccion());
        p.setFecha_registro(dto.getFecha_registro());
        p.setEstado(dto.getEstado());
        p.setEmpresa(empOpt.get());

        Proveedores updated = serviceProveedores.guardar(p);
        return ResponseEntity.ok(toDto(updated));
    }

    // ✅ Eliminar proveedor (borrado lógico)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        serviceProveedores.eliminar(id);
        return ResponseEntity.ok("Proveedor eliminado correctamente.");
    }

    // ✅ Método para mapear entidad a DTO
    private ProveedorDTO toDto(Proveedores p) {
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
        return dto;
    }
}

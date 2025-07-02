package com.gadbacorp.api.controller.administrable;

import java.util.ArrayList;
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
import com.gadbacorp.api.entity.empleados.Usuarios;
import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.repository.administrable.EmpresasRepository;
import com.gadbacorp.api.repository.empleados.UsuarioRepository;
import com.gadbacorp.api.repository.inventario.AlmacenesRepository;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.service.administrable.ISucursalesService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/minimarket/sucursales")
public class SucursalesController {

    @Autowired
    private ISucursalesService sucursalesService;
    @Autowired private InventarioRepository inventarioRepository;

    @Autowired private AlmacenesRepository     almacenesRepo;
    @Autowired
    private EmpresasRepository empresasRepository;
    @Autowired
    private UsuarioRepository usuariosRepo;
    
    @Autowired
    private InventarioProductoRepository inventarioProductoRepository;
    

    /** Listado mapeado a DTO */
    @GetMapping
    public List<SucursalDTO> buscarTodos() {
        return sucursalesService.buscarTodos().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }
    @GetMapping("/{idSucursal}/almacenes")
    public ResponseEntity<List<Almacenes>> listarAlmacenesPorSucursal(@PathVariable Integer idSucursal) {
        List<Almacenes> almacenes = almacenesRepo.findBySucursalIdSucursal(idSucursal);
        if (almacenes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(almacenes);
    }

    @GetMapping("/{idSucursal}/empresa")
public ResponseEntity<Empresas> obtenerEmpresaPorSucursal(@PathVariable Integer idSucursal) {
    Optional<Sucursales> sucursalOpt = sucursalesService.buscarId(idSucursal);

    if (sucursalOpt.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    Empresas empresa = sucursalOpt.get().getEmpresa();

    if (empresa == null) {
        return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(empresa);
}


@GetMapping("/{idSucursal}/usuarios")
public ResponseEntity<List<Usuarios>> listarUsuariosPorSucursal(@PathVariable Integer idSucursal) {
    List<Usuarios> usuarios = usuariosRepo.findBySucursal_IdSucursal(idSucursal);
    if (usuarios.isEmpty()) {
        return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(usuarios);
}
  
    
@GetMapping("/{idSucursal}/productos")
public ResponseEntity<List<InventarioProducto>> listarProductosPorSucursal(@PathVariable Integer idSucursal) {

    // 1. Obtener almacenes de la sucursal
    List<Almacenes> almacenes = almacenesRepo.findBySucursalIdSucursal(idSucursal);
    if (almacenes.isEmpty()) {
        return ResponseEntity.noContent().build();
    }

    // Lista acumuladora de todos los productos
    List<InventarioProducto> todosProductos = new ArrayList<>();

    // 2. Por cada almacén
    for (Almacenes almacen : almacenes) {
        // 2.1 Obtener inventarios del almacén
        List<Inventario> inventarios = inventarioRepository.findByAlmacen_Idalmacen(almacen.getIdalmacen());

        // 2.2 Por cada inventario
        for (Inventario inv : inventarios) {
            // 2.3 Obtener productos del inventario
            List<InventarioProducto> productos = inventarioProductoRepository.findByInventario_Idinventario(inv.getIdinventario());

            // 2.4 Agregar productos a la lista total
            todosProductos.addAll(productos);
        }
    }

    if (todosProductos.isEmpty()) {
        return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(todosProductos);
}


    /** Busqueda por id mapeada a 
     * DTO */
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
        Optional<Empresas> empOpt = empresasRepository.findById(dto.getId_empresa());
        if (empOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                .body("Empresa no encontrada con ID: " + dto.getId_empresa());
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
        Optional<Empresas> empOpt = empresasRepository.findById(dto.getId_empresa());
        if (empOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                .body("Empresa no encontrada con ID: " + dto.getId_empresa());
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
        dto.setId_empresa(s.getEmpresa().getIdempresa());
        dto.setEmpresaNombre(s.getEmpresa().getRazonsocial());
        return dto;
    }
}

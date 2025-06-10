package com.gadbacorp.api.controller.administrable;

import java.util.List;
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

import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.entity.administrable.SucursalDTO;
import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.repository.administrable.EmpresasRepository;
import com.gadbacorp.api.service.administrable.ISucursalesService;
@RestController
@RequestMapping("/api/minimarket/sucursales")
public class SucursalesController {

    @Autowired
    private ISucursalesService sucursalesService;
      @Autowired
    private EmpresasRepository empresasRepository;
    
    @GetMapping
    public List<Sucursales> buscarTodos() {
        return sucursalesService.buscarTodos();
    }
    @GetMapping("/{id}")
    public Optional<Sucursales> buscarVenta(@PathVariable Integer id) {
        return sucursalesService.buscarId(id);
    }
   
    @PostMapping
    public ResponseEntity<?> guardarVenta(@RequestBody SucursalDTO dto) {
         Empresas empresas = empresasRepository.findById(dto.getId_empresa()).orElse(null);
        if (empresas == null) {
            return ResponseEntity.badRequest().body("Empresa no encontrado con ID: " + dto.getId_empresa());
        }

        Sucursales sucursales = new Sucursales();
        sucursales.setContacto(dto.getContacto());
        sucursales.setDireccion(dto.getDireccion());
        sucursales.setNombreSucursal(dto.getNombreSucursal());
        sucursales.setEmpresa(empresas);
       
        return ResponseEntity.ok(sucursalesService.guardar(sucursales));
    }

    @PutMapping
    public ResponseEntity<?> actualizar( @RequestBody SucursalDTO dto) {
        if (dto.getIdSucursal() == null) {
            return ResponseEntity.badRequest().body("Id no existe");
        }
        
        Sucursales sucursales = new Sucursales();
        sucursales.setIdSucursal(dto.getIdSucursal());
        sucursales.setContacto(dto.getContacto());
        sucursales.setDireccion(dto.getDireccion());
        sucursales.setNombreSucursal(dto.getNombreSucursal());
        sucursales.setEmpresa(new Empresas(dto.getId_empresa()));
        return ResponseEntity.ok(sucursalesService.guardar(sucursales));
    }

    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable Integer id) {
        sucursalesService.eliminar(id);
    return "Sucursal eliminada correctamente.";
}
}
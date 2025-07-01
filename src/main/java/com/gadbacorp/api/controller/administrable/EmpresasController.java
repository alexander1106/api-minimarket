package com.gadbacorp.api.controller.administrable;

import java.sql.Blob;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.service.administrable.IEmpresasService;

@RestController
@RequestMapping("/api/minimarket/empresas")
@CrossOrigin("*")
public class EmpresasController {

    @Autowired
    private IEmpresasService serviceEmpresas;

    @GetMapping
    public List<Empresas> buscarTodos() {
        return serviceEmpresas.buscarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Empresas> buscarId(@PathVariable Integer id) {
        return serviceEmpresas.buscarId(id);
    }

    @PostMapping(
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> guardar(
        @RequestParam String razonsocial,
        @RequestParam String ciudad,
        @RequestParam String direccion,
        @RequestParam String ruc,
        @RequestParam String correo,
        @RequestParam(required = false, defaultValue = "0") Integer cant_sucursales,
        @RequestParam(required = false, defaultValue = "0") Integer cant_cajas,
        @RequestParam(required = false, defaultValue = "0") Integer cant_trabajadores,
        @RequestParam(required = false, defaultValue = "0") Integer limit_inventario,
        @RequestParam String fechaRegistro,                // "YYYY-MM-DD"
        @RequestParam Integer estado,
        @RequestParam(required = false) MultipartFile logo  // <--- aquí
    ) throws Exception {
        Empresas e = new Empresas();
        e.setRazonsocial(razonsocial);
        e.setCiudad(ciudad);
        e.setDireccion(direccion);
        e.setRuc(ruc);
        e.setCorreo(correo);
        e.setCant_sucursales(cant_sucursales);
        e.setCant_cajas(cant_cajas);
        e.setCant_trabajadores(cant_trabajadores);
        e.setLimit_inventario(limit_inventario);
        e.setFechaRegistro(LocalDate.parse(fechaRegistro));
        e.setEstado(estado);

        if (logo != null && !logo.isEmpty()) {
            // convierte MultipartFile a java.sql.Blob
            Blob blob = new SerialBlob(logo.getBytes());
            e.setLogo(blob);
        }

        serviceEmpresas.guardar(e);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
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
        @RequestParam(required = false) MultipartFile logo
    ) throws Exception {
        // primero recuperas la entidad existente (o creas con el id)
        Empresas e = serviceEmpresas.buscarId(idempresa)
                         .orElse(new Empresas());
        e.setIdempresa(idempresa);
        e.setRazonsocial(razonsocial);
        e.setCiudad(ciudad);
        e.setDireccion(direccion);
        e.setRuc(ruc);
        e.setCorreo(correo);
        e.setCant_sucursales(cant_sucursales);
        e.setCant_cajas(cant_cajas);
        e.setCant_trabajadores(cant_trabajadores);
        e.setLimit_inventario(limit_inventario);
        e.setFechaRegistro(LocalDate.parse(fechaRegistro));
        e.setEstado(estado);

        if (logo != null && !logo.isEmpty()) {
            e.setLogo(new SerialBlob(logo.getBytes()));
        }

        serviceEmpresas.modificar(e);
        return ResponseEntity.ok(e);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id){
        serviceEmpresas.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
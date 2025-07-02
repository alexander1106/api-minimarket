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
import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.repository.administrable.SucursalesRepository;
import com.gadbacorp.api.service.administrable.IEmpresasService;

@RestController
@RequestMapping("/api/minimarket/empresas")
@CrossOrigin("*")
public class EmpresasController {

    @Autowired
    private IEmpresasService serviceEmpresas;
    
    @Value("${upload.dir}") 
    private String uploadDir;

    @Autowired
    private SucursalesRepository sucursalesRepository;

    @GetMapping
    public List<Empresas> buscarTodos() throws IOException {
        List<Empresas> empresas = serviceEmpresas.buscarTodos();
        for (Empresas e : empresas) {
            if (e.getLogo() != null) {
                Path imgPath = Paths.get(uploadDir).resolve(e.getLogo()).toAbsolutePath().normalize();
                if (Files.exists(imgPath)) {
                    byte[] bytes = Files.readAllBytes(imgPath);
                    String base64 = Base64.getEncoder().encodeToString(bytes);
                    e.setLogo(base64); // Sobrescribimos nombre archivo con base64 para enviar al frontend
                } else {
                    e.setLogo(null);
                }
            }
        }
        return empresas;
    }

    @GetMapping("/{id}")
    public Optional<Empresas> buscarId(@PathVariable Integer id) {
        return serviceEmpresas.buscarId(id);
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
        @RequestParam(required = false) MultipartFile logo
    ) throws IOException {

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
        @RequestParam(required = false) MultipartFile logo
    ) throws Exception {
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
            String fileName = "empresa_" + System.currentTimeMillis() + "_" + logo.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            Path targetPath = uploadPath.resolve(fileName);
            logo.transferTo(targetPath.toFile());
            e.setLogo(fileName);
        }

        serviceEmpresas.modificar(e);

        // Cargar logo en base64 para enviar al frontend
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

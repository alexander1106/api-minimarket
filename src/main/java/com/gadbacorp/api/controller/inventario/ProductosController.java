package com.gadbacorp.api.controller.inventario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.inventario.ProductosDTO;
import com.gadbacorp.api.service.inventario.IProductosService;

@RestController
@RequestMapping("/api/minimarket/productos")
public class ProductosController {

    @Autowired
    private IProductosService serviceProductos;

    // Evita que WebDataBinder intente mapear directamente 'imagen' al DTO
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("imagen");
    }

    @GetMapping
    public ResponseEntity<List<ProductosDTO>> listar() {
        return ResponseEntity.ok(serviceProductos.buscarTodosDTO());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductosDTO> obtener(@PathVariable Integer id) {
        return serviceProductos.buscarIdDTO(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST JSON
    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> crearJson(@RequestBody ProductosDTO dto) {
        try {
            ProductosDTO creado = serviceProductos.guardarDTO(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al crear producto");
        }
    }

    // POST form-data plano: campos + archivo bajo 'imagen'
    @PostMapping(
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> crearForm(
        @ModelAttribute ProductosDTO dto,
        @RequestParam(value = "imagen", required = false) MultipartFile imagenFile
    ) {
        try {
            if (imagenFile != null && !imagenFile.isEmpty()) {
                dto.setImagen(imagenFile.getOriginalFilename());
            }
            ProductosDTO creado = serviceProductos.guardarDTO(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al crear producto");
        }
    }

    // PUT JSON (id en body)
    @PutMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> actualizarJson(@RequestBody ProductosDTO dto) {
        try {
            Integer id = dto.getIdproducto();
            if (id == null) {
                return ResponseEntity.badRequest().body("El campo 'idproducto' es requerido en el body");
            }
            ProductosDTO actualizado = serviceProductos.actualizarDTO(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al actualizar producto");
        }
    }

    // PUT form-data plano: campos + archivo bajo 'imagen'
    @PutMapping(
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> actualizarForm(
        @ModelAttribute ProductosDTO dto,
        @RequestParam(value = "imagen", required = false) MultipartFile imagenFile
    ) {
        try {
            Integer id = dto.getIdproducto();
            if (id == null) {
                return ResponseEntity.badRequest().body("El campo 'idproducto' es requerido en el body");
            }
            if (imagenFile != null && !imagenFile.isEmpty()) {
                dto.setImagen(imagenFile.getOriginalFilename());
            }
            ProductosDTO actualizado = serviceProductos.actualizarDTO(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al actualizar producto");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        serviceProductos.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

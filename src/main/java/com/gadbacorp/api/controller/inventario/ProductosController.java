package com.gadbacorp.api.controller.inventario;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gadbacorp.api.entity.inventario.ProductosDTO;
import com.gadbacorp.api.service.jpa.inventario.ProductosService;

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Inventario - Productos")
@RestController
@RequestMapping("/api/minimarket")
public class ProductosController {

    @Autowired
    private ProductosService productosService;

    @Value("${upload.dir}")
    private String uploadDir;

    @GetMapping("/productos")
    public ResponseEntity<List<ProductosDTO>> listar() {
        return ResponseEntity.ok(productosService.listarProductosDTO());
    }

    @GetMapping("/productos/stock-bajo")
    public ResponseEntity<List<java.util.Map<String, Object>>> stockBajo(
            @RequestParam(required = false) Integer idSucursal) {
        return ResponseEntity.ok(productosService.stockBajo(idSucursal));
    }

    /** Para el lector de codigos del punto de venta: codigo de barras, SKU o codigo de barras de una presentacion. */
    @GetMapping("/productos/codigo/{codigo}")
    public ResponseEntity<java.util.Map<String, Object>> porCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(productosService.buscarPorCodigo(codigo));
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<ProductosDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(productosService.obtenerProductoDTO(id));
    }

    @GetMapping("/productos/{id}/categoria")
    public ResponseEntity<List<ProductosDTO>> listarPorCategoria(@PathVariable Integer id) {
        return ResponseEntity.ok(productosService.listarProductosPorCategoriaDTO(id));
    }

    @PostMapping("/productos")
    public ResponseEntity<ProductosDTO> crear(@RequestBody ProductosDTO dto) {
        ProductosDTO creado = productosService.crearProducto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/productos")
    public ResponseEntity<ProductosDTO> actualizar(@RequestBody ProductosDTO dto) {
        ProductosDTO actualizado = productosService.actualizarProducto(dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        productosService.eliminarProducto(id);
        return ResponseEntity.ok("Producto eliminado correctamente");
    }

    @PostMapping(
      value    = "/productos/upload",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> subirImagen(@RequestParam("file") MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            Path target = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok(filename);

        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error guardando archivo: " + ex.getMessage());
        }
    }
}

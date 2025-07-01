package com.gadbacorp.api.controller.inventario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.gadbacorp.api.entity.inventario.ProductosDTO;
import com.gadbacorp.api.service.jpa.inventario.ProductosService;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductosController {

    @Autowired
    private ProductosService productosService;

    @GetMapping("/productos")
    public ResponseEntity<List<ProductosDTO>> listar() {
        return ResponseEntity.ok(productosService.listarProductosDTO());
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
}

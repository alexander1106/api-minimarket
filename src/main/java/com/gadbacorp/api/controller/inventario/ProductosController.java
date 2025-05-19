package com.gadbacorp.api.controller.inventario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.inventario.ProductosDTO;
import com.gadbacorp.api.service.inventario.IProductosService;

@RestController
@RequestMapping("/api/minimarket/productos")
public class ProductosController {

    @Autowired
    private IProductosService serviceProductos;

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

    @PostMapping
    public ResponseEntity<ProductosDTO> crear(@RequestBody ProductosDTO dto) {
        ProductosDTO creado = serviceProductos.guardarDTO(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductosDTO> actualizar(@PathVariable Integer id, @RequestBody ProductosDTO dto) {
        ProductosDTO actualizado = serviceProductos.actualizarDTO(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        serviceProductos.eliminar(id);
        return ResponseEntity.ok("Producto eliminado");
    }
}

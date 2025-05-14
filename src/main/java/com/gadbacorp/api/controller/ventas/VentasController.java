package com.gadbacorp.api.controller.ventas;

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

import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.service.ventas.IVentasService;


@RestController
@RequestMapping("/api/minimarket")
public class VentasController {

    @Autowired
    private IVentasService ventasService;

    // Guardar nueva venta
    @PostMapping("/ventas")
    public ResponseEntity<Ventas> guardarVenta(@RequestBody Ventas venta) {
        ventasService.guardarVenta(venta);
        return ResponseEntity.ok(venta);
    }

    // Editar venta
    @PutMapping("/ventas/{id}")
    public ResponseEntity<Ventas> editarVenta(@PathVariable Integer id, @RequestBody Ventas venta) {
        Optional<Ventas> ventaExistente = ventasService.buscarVenta(id);
        if (ventaExistente.isPresent()) {
            venta.setIdVenta(id); // Asegúrate que se actualiza correctamente
            ventasService.editarVenta(venta);
            return ResponseEntity.ok(venta);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar venta
    @DeleteMapping("/ventas/{id}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Integer id) {
        ventasService.eliminarVenta(id);
        return ResponseEntity.noContent().build();
    }

    // Buscar venta por ID
    @GetMapping("/{id}")
    public ResponseEntity<Ventas> buscarVenta(@PathVariable Integer id) {
        Optional<Ventas> venta = ventasService.buscarVenta(id);
        return venta.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Listar todas las ventas
    @GetMapping
    public ResponseEntity<List<Ventas>> listarVentas() {
        return ResponseEntity.ok(ventasService.listarVentaas());
    }

    // Obtener ventas por ID de cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Ventas>> obtenerVentasPorCliente(@PathVariable Integer clienteId) {
        return ResponseEntity.ok(ventasService.obtenerVentasPorCliente(clienteId));
    }
}
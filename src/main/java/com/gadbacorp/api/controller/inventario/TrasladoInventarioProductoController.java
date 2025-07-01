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

import com.gadbacorp.api.entity.inventario.TrasladoInventarioProductoDTO;
import com.gadbacorp.api.service.jpa.inventario.TrasladoInventarioProductoService;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin(origins = "http://localhost:4200")
public class TrasladoInventarioProductoController {

    @Autowired
    private TrasladoInventarioProductoService svc;

    @GetMapping("/traslados")
    public ResponseEntity<List<TrasladoInventarioProductoDTO>> listar() {
        return ResponseEntity.ok(svc.listarTodos());
    }

    @GetMapping("/traslados/{id}")
    public ResponseEntity<TrasladoInventarioProductoDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(svc.obtenerPorId(id));
    }

    @PostMapping("/traslados")
    public ResponseEntity<TrasladoInventarioProductoDTO> crear(
            @RequestBody TrasladoInventarioProductoDTO dto) {
        TrasladoInventarioProductoDTO creado = svc.crearTraslado(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/traslados")
    public ResponseEntity<TrasladoInventarioProductoDTO> actualizar(
            @RequestBody TrasladoInventarioProductoDTO dto) {
        // Esto siempre lanzará 405 con mensaje "No está permitido editar traslados"
        TrasladoInventarioProductoDTO upd = svc.actualizarTraslado(dto);
        return ResponseEntity.ok(upd);
    }

    @DeleteMapping("/traslados/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        // Esto siempre lanzará 405 con mensaje "No está permitido eliminar traslados"
        svc.eliminarTraslado(id);
        return ResponseEntity.ok("Este mensaje nunca se llega a enviar");
    }
}

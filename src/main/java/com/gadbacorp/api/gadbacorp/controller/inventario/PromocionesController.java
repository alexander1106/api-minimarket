package com.gadbacorp.api.gadbacorp.controller.inventario;

import java.util.List;
import java.util.Optional;

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
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.gadbacorp.entity.inventario.PromocionesDTO;
import com.gadbacorp.api.gadbacorp.service.inventario.IPromocionesService;

@RestController
@RequestMapping("/api/minimarket/promociones")
public class PromocionesController {

    @Autowired
    private IPromocionesService servicePromociones;

    @GetMapping
    public ResponseEntity<List<PromocionesDTO>> listar() {
        return ResponseEntity.ok(servicePromociones.buscarTodosDTO());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromocionesDTO> obtener(@PathVariable Integer id) {
        Optional<PromocionesDTO> dto = servicePromociones.buscarIdDTO(id);
        return dto.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PromocionesDTO> crear(@RequestBody PromocionesDTO dto) {
        validarContenido(dto);
        PromocionesDTO creado = servicePromociones.guardarDTO(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromocionesDTO> actualizar(@PathVariable Integer id, @RequestBody PromocionesDTO dto) {
        validarContenido(dto);
        PromocionesDTO actualizado = servicePromociones.actualizarDTO(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        servicePromociones.eliminar(id);
        return ResponseEntity.ok("Promoción eliminada");
    }

    private void validarContenido(PromocionesDTO dto) {
        if ((dto.getProductos() == null || dto.getProductos().isEmpty()) &&
            (dto.getCategorias() == null || dto.getCategorias().isEmpty())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Debe especificar al menos un producto o una categoría para la promoción.");
        }
    }
}

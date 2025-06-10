package com.gadbacorp.api.controller.inventario;

import java.util.List;
import java.util.stream.Collectors;

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

import com.gadbacorp.api.entity.inventario.UnidadDeMedida;
import com.gadbacorp.api.entity.inventario.UnidadDeMedidaDTO;
import com.gadbacorp.api.service.inventario.IUnidadDeMedidaService;

@RestController
@RequestMapping("/api/minimarket")
public class UnidadDeMedidaController {

    @Autowired
    private IUnidadDeMedidaService service;

    private UnidadDeMedidaDTO toDTO(UnidadDeMedida entity) {
        return new UnidadDeMedidaDTO(entity.getIdunidadmedida(), entity.getNombre());
    }

    private UnidadDeMedida toEntity(UnidadDeMedidaDTO dto) {
        UnidadDeMedida entity = new UnidadDeMedida();
        entity.setIdunidadmedida(dto.getIdunidadmedida());
        entity.setNombre(dto.getNombre());
        return entity;
    }

    @GetMapping("/unidad_medida")
    public List<UnidadDeMedidaDTO> listar() {
        return service.buscarTodos().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/unidad_medida/{id}")
    public ResponseEntity<UnidadDeMedidaDTO> buscar(@PathVariable Integer id) {
        return service.buscarId(id)
                .map(u -> ResponseEntity.ok(toDTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/unidad_medida")
    public UnidadDeMedidaDTO guardar(@RequestBody UnidadDeMedidaDTO dto) {
        return toDTO(service.guardar(toEntity(dto)));
    }

    @PutMapping("/unidad_medida")
    public UnidadDeMedidaDTO modificar(@RequestBody UnidadDeMedidaDTO dto) {
        return toDTO(service.modificar(toEntity(dto)));
    }

    @DeleteMapping("/unidad_medida/{id}")
    public String eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return "Unidad de medida eliminada";
    }

}
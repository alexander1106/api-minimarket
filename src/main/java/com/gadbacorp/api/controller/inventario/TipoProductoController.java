package com.gadbacorp.api.controller.inventario;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.inventario.TipoProducto;
import com.gadbacorp.api.entity.inventario.TipoProductoDTO;
import com.gadbacorp.api.service.inventario.ITipoProductoService;

@RestController
@RequestMapping("/api/minimarket")
public class TipoProductoController {

    @Autowired
    private ITipoProductoService service;

    private TipoProductoDTO toDTO(TipoProducto entity) {
        return new TipoProductoDTO(entity.getIdtipoproducto(), entity.getNombre());
    }

    private TipoProducto toEntity(TipoProductoDTO dto) {
        TipoProducto entity = new TipoProducto();
        entity.setIdtipoproducto(dto.getIdtipoproducto());
        entity.setNombre(dto.getNombre());
        return entity;
    }

    @GetMapping("/tipoproducto")
    public List<TipoProductoDTO> listar() {
        return service.buscarTodos().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/tipoproducto/{id}")
    public TipoProductoDTO buscar(@PathVariable Integer id) {
        Optional<TipoProducto> tipo = service.buscarId(id);
        return tipo.map(this::toDTO).orElse(null);
    }

    @PostMapping("/tipoproducto")
    public TipoProductoDTO guardar(@RequestBody TipoProductoDTO dto) {
        return toDTO(service.guardar(toEntity(dto)));
    }

    @PutMapping("/tipoproducto")
    public TipoProductoDTO modificar(@RequestBody TipoProductoDTO dto) {
        return toDTO(service.modificar(toEntity(dto)));
    }

    @DeleteMapping("/tipoproducto/{id}")
    public String eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return "Tipo de producto eliminado";
    }
}

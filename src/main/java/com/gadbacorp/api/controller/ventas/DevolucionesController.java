package com.gadbacorp.api.controller.ventas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.entity.ventas.Devoluciones;
import com.gadbacorp.api.entity.ventas.DevolucionesDTO;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.repository.ventas.VentasRepository;
import com.gadbacorp.api.service.ventas.IDevolucionesService;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin("*")
public class DevolucionesController {
    @Autowired
    private IDevolucionesService devolucionesService;
    @Autowired
    private VentasRepository ventasRepository;

    @GetMapping("/devoluciones")
    public List<Devoluciones> listarTodos() {
        return devolucionesService.listarDevoluciones();
    }

    @GetMapping("/devolucioones/{id}")
    public  Optional<Devoluciones> buscarDevolucion(@PathVariable Integer id){
        return  devolucionesService.buscarDevolucion(id);
    }

    @PostMapping("/devolucion")
    public ResponseEntity<?> guardarDevolucion(@RequestBody DevolucionesDTO dto){
        Ventas venta = ventasRepository.findById(dto.getId_venta()).orElse(null);
        if (venta == null) {
            return ResponseEntity.badRequest().body("Venta no encontrada con ID: " + dto.getId_venta());
        }
        Devoluciones devolucion = new Devoluciones();
        devolucion.setEstadoDevolucion(dto.getEstadoDevolucion());
        devolucion.setFechaDevolucion(dto.getFechaDevolucion());
        devolucion.setMontoDevuelto(dto.getMontoDevuelto());
        devolucion.setMotivoDevolucion(dto.getMotivoDevolucion());
        devolucion.setObservaciones(dto.getObservaciones());
        devolucion.setVentas(venta);
        return ResponseEntity.ok(devolucionesService.guardarDevoluciones(devolucion));
    }

    @PutMapping("devolucion")
    public ResponseEntity<?> modificarDevolucion(@RequestBody DevolucionesDTO dto){
        if(dto.getId_devolucion() == null){
            return ResponseEntity.badRequest().body("Id no existe");
        }
        Devoluciones devolucion = new Devoluciones();
        devolucion.setId_devolucion(dto.getId_devolucion());
        devolucion.setEstadoDevolucion(dto.getEstadoDevolucion());
        devolucion.setFechaDevolucion(dto.getFechaDevolucion());
        devolucion.setMontoDevuelto(dto.getMontoDevuelto());
        devolucion.setMotivoDevolucion(dto.getMotivoDevolucion());
        devolucion.setObservaciones(dto.getObservaciones());
        devolucion.setVentas(new Ventas(dto.getId_venta()));
        return ResponseEntity.ok(devolucionesService.editarDevolucion(devolucion));
    }

    @DeleteMapping("devolucion/{id}")
    public String eliminarDevoluciones(@PathVariable Integer id){
        devolucionesService.elimmanrDevolucion(id);
        return "La devolucion a sido eliminada con exito";
    } 
}

package com.gadbacorp.api.controller.ventas;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.entity.ventas.Pagos;
import com.gadbacorp.api.entity.ventas.PagosDTO;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.repository.ventas.MetodosPagoRepository;
import com.gadbacorp.api.repository.ventas.PagosRepository;
import com.gadbacorp.api.repository.ventas.VentasRepository;
import com.gadbacorp.api.service.ventas.IPagosService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/minimarket")
public class PagosController {

    @Autowired
    private IPagosService pagosService;

    @Autowired
    private MetodosPagoRepository metodosPagoRepository;
    
    @Autowired
    private VentasRepository ventasRepository;
    @Autowired
    private PagosRepository pagosRepository;

    @GetMapping("/pagos")
    public List<Pagos> listarPagos() {
        return pagosService.listarPagos();
    }
@PostMapping("/pagos")
public ResponseEntity<?> guardar(@RequestBody PagosDTO dto) {
    Pagos pago = new Pagos();
    pago.setEstadoPago(dto.getEstadoPago());
    pago.setObservaciones(dto.getObservaciones());
    pago.setReferenciaPago(dto.getReferenciaPago());
    pago.setFechaPago(LocalDate.now());
    pago.setMontoPagado(dto.getMontoPagado());

    MetodosPago metodo = metodosPagoRepository.findById(dto.getId_metodo_pago()).orElse(null);

    if (metodo == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Metodo no encontrado.");
    }

    Ventas venta = ventasRepository.findById(dto.getId_venta()).orElse(null);
    if (venta == null) {
        // Si no existe, crear una nueva venta
        venta = new Ventas();
        venta.setEstado_venta("PAGADO");
        venta.setFecha_venta(LocalDate.now());
        // venta.setCliente(cliente);
        // venta.setFechaVenta(new Date());
        venta.setTotal_venta(pago.getMontoPagado());
        venta = ventasRepository.save(venta);
    } else {
        // Si existe, actualizar el estado
        venta.setEstado_venta("PAGADO");
        ventasRepository.save(venta);
    }

    pago.setVentas(venta);
    pago.setMetodosPago(metodo);

    return ResponseEntity.ok(pagosService.guardarPago(pago));
}


     @GetMapping("/pagos/{id}")
    public Optional<Pagos> buscarId(@PathVariable("id") Integer id){
        return pagosService.buscarPago(id);
    }

    @PutMapping("/pagos")
    public ResponseEntity<?> modificar(@RequestBody PagosDTO dto) {
        if (dto.getIdPago() == null) {
            return ResponseEntity.badRequest().body("El ID del pago es obligatorio para modificar.");
        }

        Optional<Pagos> optionalPago = pagosRepository.findById(dto.getIdPago());
        if (!optionalPago.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pago no encontrado con ID: " + dto.getIdPago());
        }

        Pagos pago = optionalPago.get();
        pago.setEstadoPago(dto.getEstadoPago());
        pago.setObservaciones(dto.getObservaciones());
        pago.setReferenciaPago(dto.getReferenciaPago());
        pago.setFechaPago(LocalDate.now());
        pago.setMontoPagado(dto.getMontoPagado());

        // Relaciones
        MetodosPago metodo = metodosPagoRepository.findById(dto.getId_metodo_pago()).orElse(null);
        pago.setMetodosPago(metodo);

        Ventas venta = ventasRepository.findById(dto.getId_venta()).orElse(null);
        pago.setVentas(venta);

        Pagos pagoActualizado = pagosService.guardarPago(pago);
        return ResponseEntity.ok(pagoActualizado);
    }

    @DeleteMapping("/pagos/{id}")
    public String eliminar(@PathVariable Integer id){
        pagosService.eliminarPago(id);
        return "El pago fue eliminado";
    }

}

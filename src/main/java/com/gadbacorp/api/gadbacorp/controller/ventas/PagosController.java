package com.gadbacorp.api.gadbacorp.controller.ventas;

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

import com.gadbacorp.api.gadbacorp.entity.dto.ventas.PagosDTO;
import com.gadbacorp.api.gadbacorp.entity.ventas.MetodosPago;
import com.gadbacorp.api.gadbacorp.entity.ventas.Pagos;
import com.gadbacorp.api.gadbacorp.repository.ventas.MetodosPagoRepository;
import com.gadbacorp.api.gadbacorp.repository.ventas.PagosRepository;
import com.gadbacorp.api.gadbacorp.service.ventas.IMetodosPagoService;
import com.gadbacorp.api.gadbacorp.service.ventas.IPagosService;

@RestController
@RequestMapping("/api/minimarket")
public class PagosController {

    @Autowired
    private IMetodosPagoService metodosPagoService;

    @Autowired
    private IPagosService pagosService;
    @Autowired
    private MetodosPagoRepository metodosPagoRepository;

    @PostMapping("/pagos")
    public ResponseEntity<?> guardar(@RequestBody PagosDTO dto) {
        Pagos pago = new Pagos();
        pago.setComprobante(dto.getComprobante());
        pago.setDescripcion((dto.getDescripcion()));
        pago.setMonto(dto.getMonto());
        pago.setFechaPago(dto.getFechaPago());
        pago.setEstadoPago(dto.getEstadoPago());
        MetodosPago metodo = metodosPagoRepository.findById(dto.getIdPago()).orElse(null);
        pago.setMetodosPago(metodo);
       return ResponseEntity.ok(pagosService.guardarPago(pago));        
    }


}

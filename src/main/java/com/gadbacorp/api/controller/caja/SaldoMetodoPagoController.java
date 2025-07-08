package com.gadbacorp.api.controller.caja;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.caja.AperturaCaja;
import com.gadbacorp.api.entity.caja.SaldoMetodoPago;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.service.jpa.cajas.SaldoMetodoPagoService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/minimarket/saldo-metodo")
public class SaldoMetodoPagoController {

    @Autowired
    private SaldoMetodoPagoService saldoMetodoPagoService;

    @GetMapping("/apertura/{idAperturaCaja}")
    public List<SaldoMetodoPago> listarPorApertura(@PathVariable Integer idAperturaCaja) {
        return saldoMetodoPagoService.obtenerSaldosPorApertura(idAperturaCaja);
    }
    @GetMapping("/apertura/{idAperturaCaja}/metodo/{idMetodoPago}")
    public SaldoMetodoPago obtenerPorAperturaYMetodo(@PathVariable Integer idAperturaCaja,
                                                     @PathVariable Integer idMetodoPago) {
        AperturaCaja apertura = new AperturaCaja();
        apertura.setIdAperturaCaja(idAperturaCaja);
        MetodosPago metodo = new MetodosPago();
        metodo.setIdMetodoPago(idMetodoPago);
        return saldoMetodoPagoService.obtenerSaldoPorAperturaYMetodo(apertura, metodo);
    }

    @PostMapping
    public SaldoMetodoPago crear(@RequestBody SaldoMetodoPago saldoMetodoPago) {
        return saldoMetodoPagoService.crearSaldo(saldoMetodoPago);
    }
    
    @PutMapping("/{id}")
    public SaldoMetodoPago actualizar(@PathVariable Integer id, @RequestBody SaldoMetodoPago saldoMetodoPago) {
        saldoMetodoPago.setIdSaldoMetodoPago(id);
        return saldoMetodoPagoService.actualizarSaldo(saldoMetodoPago);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        saldoMetodoPagoService.eliminarSaldo(id);
    }
 
}
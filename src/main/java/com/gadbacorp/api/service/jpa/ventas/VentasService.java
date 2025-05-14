package com.gadbacorp.api.service.jpa.ventas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.repository.ventas.VentasRepository;
import com.gadbacorp.api.service.ventas.IVentasService;

public class VentasService implements IVentasService {

    @Autowired
    private VentasRepository ventasRepository;
    @Override
    public void guardarVenta(Ventas venta) {
        ventasRepository.save(venta);
    }

    @Override
    public void editarVenta(Ventas venta) {
        ventasRepository.save(venta);
    }

    @Override
    public void eliminarVenta(Integer idVenta) {
        ventasRepository.deleteById(idVenta);
    }

    @Override
    public Optional<Ventas> buscarVenta(Integer idVenta) {
        return ventasRepository.findById(idVenta);
    }

    @Override
    public List<Ventas> listarVentaas() {
        return ventasRepository.findAll();
    }

    @Override
    public List<Ventas> obtenerVentasPorCliente(Integer clienteId) {
        return ventasRepository.findByClienteId(clienteId);
    }

}

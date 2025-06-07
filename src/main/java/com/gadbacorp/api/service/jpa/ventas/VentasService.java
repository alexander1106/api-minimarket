package com.gadbacorp.api.service.jpa.ventas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.repository.ventas.VentasRepository;
import com.gadbacorp.api.service.ventas.IVentasService;

@Service
public class VentasService implements IVentasService {

    @Autowired
    private VentasRepository ventasRepository;
    @Override
    public Ventas guardarVenta(Ventas venta) {
        return ventasRepository.save(venta);
    }

    @Override
    public Ventas editarVenta(Ventas venta) {
        return ventasRepository.save(venta);
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
        return ventasRepository.findByClienteIdCliente(clienteId);
    }

}

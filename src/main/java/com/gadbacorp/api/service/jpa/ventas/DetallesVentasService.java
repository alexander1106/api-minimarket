package com.gadbacorp.api.service.jpa.ventas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.ventas.DetallesVentas;
import com.gadbacorp.api.repository.ventas.DetallesVentasRepository;
import com.gadbacorp.api.service.ventas.IDetallesVentasService;

@Service
public class DetallesVentasService implements IDetallesVentasService {
    @Autowired
    private DetallesVentasRepository detallesVentasRepository;

    @Override
    public DetallesVentas guardarDetallesVentas(DetallesVentas detallesVentas) {
        return detallesVentasRepository.save(detallesVentas);
    }

    @Override
    public DetallesVentas editarDetallesVentas(DetallesVentas detallesVentas) {
        return detallesVentasRepository.save(detallesVentas);
    }

    @Override
    public void eliminarDetallesVentas(Integer idDetallesVenta) {
        detallesVentasRepository.deleteById(idDetallesVenta);
    }

    @Override
    public List<DetallesVentas> listDetallesVentas() {
        return detallesVentasRepository.findAll();
    }

    @Override
    public Optional<DetallesVentas> buscarDetallesVentas(Integer IdVenta) {
        return detallesVentasRepository.findById(IdVenta);
    }

    @Override
public List<DetallesVentas> buscarPorIdVenta(Integer idVenta) {
    return detallesVentasRepository.findByVentas_IdVenta(idVenta);
}

}

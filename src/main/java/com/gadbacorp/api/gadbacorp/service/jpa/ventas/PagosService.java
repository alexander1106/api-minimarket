package com.gadbacorp.api.gadbacorp.service.jpa.ventas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.gadbacorp.entity.ventas.Pagos;
import com.gadbacorp.api.gadbacorp.repository.ventas.PagosRepository;
import com.gadbacorp.api.gadbacorp.service.ventas.IPagosService;

@Service
public class PagosService implements  IPagosService{
    @Autowired
    private PagosRepository pagosRepository;

    @Override
    public Pagos guardarPago(Pagos pago) {
        return pagosRepository.save(pago);
        }

    @Override
    public Pagos editarPago(Pagos pago) {
        return pagosRepository.save(pago);
    }

    @Override
    public void eliminarPago(Integer id) {
        pagosRepository.deleteById(id);
    }

    @Override
    public List<Pagos> listarPagos() {
        return pagosRepository.findAll();
    }

    @Override
    public Optional<Pagos> buscarPago(Integer id) {
        return pagosRepository.findById(id);
    }

}

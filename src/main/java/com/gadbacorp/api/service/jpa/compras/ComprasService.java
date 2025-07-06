package com.gadbacorp.api.service.jpa.compras;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.compras.Compras;
import com.gadbacorp.api.repository.compras.ComprasRepository;
import com.gadbacorp.api.service.compras.IComprasService;

@Service
public class ComprasService implements IComprasService {

    @Autowired
    private ComprasRepository comprasRepository;

    @Override
    public Compras guardarCompra(Compras compra) {
        return comprasRepository.save(compra);
    }

    @Override
    public Compras editarCompra(Compras compra) {
        return comprasRepository.save(compra);
    }

    @Override
    public void eliminarCompra(Integer idCompra) {
        comprasRepository.deleteById(idCompra);
    }

    @Override
    public Optional<Compras> buscarCompra(Integer idCompra) {
        return comprasRepository.findById(idCompra);
    }

    @Override
    public List<Compras> listarCompras() {
        return comprasRepository.findAll();
    }

    @Override
    public List<Compras> obtenerComprasPorProveedor(Integer idProveedor) {
        return comprasRepository.findByProveedorIdProveedor(idProveedor);
    }
}
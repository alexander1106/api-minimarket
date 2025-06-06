package com.gadbacorp.api.service.jpa.compras;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.compras.DetallesCompras;
import com.gadbacorp.api.repository.compras.DetallesComprasRepository;
import com.gadbacorp.api.service.compras.IDetallesComprasService;

@Service
public class DetallesComprasService implements IDetallesComprasService {
    
    @Autowired
    private DetallesComprasRepository repoDetallesCompras;
    
    @Override
    public List<DetallesCompras> listar() {
        return repoDetallesCompras.findAll();
    }
    
    @Override
    public DetallesCompras guardar(DetallesCompras detalleCompra) {
        return repoDetallesCompras.save(detalleCompra);
    }

    @Override
    public Optional<DetallesCompras> obtenerPorId(Integer id) {
        return repoDetallesCompras.findById(id);
    }

    @Override
    public void eliminar(Integer id) {
        repoDetallesCompras.deleteById(id);
    }
    
    @Override
    public List<DetallesCompras> listarPorCompra(Integer idCompra) {
        return repoDetallesCompras.findByCompraIdCompra(idCompra);
    }
}
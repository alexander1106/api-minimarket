package com.gadbacorp.api.service.jpa.compras;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.compras.Proveedores;
import com.gadbacorp.api.repository.compras.ProveedoresRepository;
import com.gadbacorp.api.service.compras.IProveedoresService;

@Service
public class ProveedoresService implements IProveedoresService {
    
    @Autowired
    private ProveedoresRepository repoProveedores;
    
    @Override
    public List<Proveedores> buscarTodos() {
        return repoProveedores.findAll();
    }
    
    @Override
    public Proveedores guardar(Proveedores proveedor) {
        return repoProveedores.save(proveedor); // Ahora retorna el objeto guardado
    }

    @Override
    public Proveedores modificar(Proveedores proveedor) {
        return repoProveedores.save(proveedor); // Ahora retorna el objeto modificado
    }

    @Override
    public Optional<Proveedores> buscarId(Integer id) {
        return repoProveedores.findById(id);
    }

    @Override
    public void eliminar(Integer id) {
        repoProveedores.deleteById(id);
    }
    
    @Override
    public List<Proveedores> buscarPorEmpresa(Integer idEmpresa) {
        return repoProveedores.findByEmpresaIdempresa(idEmpresa);
    }
}
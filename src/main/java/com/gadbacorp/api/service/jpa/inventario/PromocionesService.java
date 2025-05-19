package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.inventario.Promociones;
import com.gadbacorp.api.repository.inventario.PromocionesRepository;
import com.gadbacorp.api.service.inventario.IPromocionesService;

@Service
public class PromocionesService implements IPromocionesService{
    @Autowired
    private PromocionesRepository repoPromociones;
    public List<Promociones> buscarTodos(){
        return repoPromociones.findAll();
    }
    public Promociones guardar(Promociones promociones){
        return repoPromociones.save(promociones);
    }
    
    public Promociones modificar(Promociones promociones){
        return repoPromociones.save(promociones);
    }

    public Optional<Promociones> buscarId(Integer id){
        return repoPromociones.findById(id);
    }

    public void eliminar(Integer id){
        repoPromociones.deleteById(id);
    }
}

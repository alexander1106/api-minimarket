package com.gadbacorp.api.service.administrable;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.administrable.Empresas;


public interface IEmpresasService {
    // Listar todos las empresas de la tabla
    List<Empresas> buscarTodos();
    // Guarda las empresass
    void guardar(Empresas empresa);
    
    void modificar(Empresas empresa);

    Optional<Empresas> buscarId(Integer id);

    void eliminar(Integer id);

}

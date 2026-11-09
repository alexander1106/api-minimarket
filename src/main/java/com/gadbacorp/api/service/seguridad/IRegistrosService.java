package com.gadbacorp.api.service.seguridad;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.seguridad.Registros;

public interface IRegistrosService {
    List<Registros> buscarTodos();
    void guardar(Registros registro);

    void modificar(Registros registro);

    Optional<Registros> buscarId(Integer id);
    
    void eliminar(Integer id);
    
}

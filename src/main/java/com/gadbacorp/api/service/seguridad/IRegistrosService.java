package com.gadbacorp.api.service.seguridad;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.seguridad.Registros;

public interface IRegistrosService {
    // Listar todos los registros de la tabla
    List<Registros> buscarTodos();
    // Guarda los registros
    void guardar(Registros registro);

    void modificar(Registros registro);

    Optional<Registros> buscarId(Integer id);
    
    void eliminar(Integer id);
    
}

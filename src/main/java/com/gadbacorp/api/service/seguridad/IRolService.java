package com.gadbacorp.api.service.seguridad;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.seguridad.Rol;

public interface IRolService {
    List<Rol> listarTodos();
    Optional<Rol> obtenerPorId(Integer id);
    Rol guardar(Rol rol);
    Rol actualizar(Rol rol);
    void eliminar(Integer id);
}

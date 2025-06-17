package com.gadbacorp.api.service.seguridad;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.seguridad.RolModulo;

public interface IRolModuloService {
    List<RolModulo> listarTodos();
    Optional<RolModulo> obtenerPorId(Integer id);
    RolModulo guardar(RolModulo rol);
    RolModulo actualizar(RolModulo rol);
    void eliminar(Integer id);
}

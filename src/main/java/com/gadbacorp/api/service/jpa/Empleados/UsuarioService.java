package com.gadbacorp.api.service.jpa.Empleados;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.empleados.Usuarios;
import com.gadbacorp.api.repository.empleados.UsuarioRepository;
import com.gadbacorp.api.service.empleados.IUsuariosService;

@Service
public class UsuarioService implements IUsuariosService {

   @Autowired
   private UsuarioRepository repoEmpleado;
   @Override
   public List<Usuarios> buscarTodos() {
      return repoEmpleado.findAll();
   }

   @Override
   public Usuarios guardar(Usuarios empleado) {
      return repoEmpleado.save(empleado);
   }

   @Override
   public Usuarios modificar(Usuarios empleado) {
      return repoEmpleado.save(empleado);
   }

   @Override
   public Optional<Usuarios> buscarId(Integer id) {
      return repoEmpleado.findById(id);
   }

  @Override
    public void eliminar(Integer id) {
        repoEmpleado.deleteById(id);
    }

}

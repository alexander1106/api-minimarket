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
   private UsuarioRepository usuariosRepository;
   @Override
   public List<Usuarios> buscarTodos() {
      return usuariosRepository.findAll();
   }

   @Override
   public Usuarios guardar(Usuarios empleado) {
      return usuariosRepository.save(empleado);
   }
   

   @Override
   public Usuarios modificar(Usuarios empleado) {
      return usuariosRepository.save(empleado);
   }

   @Override
   public Optional<Usuarios> buscarId(Integer id) {
      return usuariosRepository.findById(id);
   }

  @Override
    public void eliminar(Integer id) {
        usuariosRepository.deleteById(id);
    }
  
    @Override
    public Usuarios obtenerUsuario(String username) {
        return usuariosRepository.findByUsername(username);
    }
    @Override
    public Optional<Usuarios> buscarPorUsername(String username) {
        // Llama al nuevo método del repo
        return usuariosRepository.findOptionalByUsername(username);
    }


}

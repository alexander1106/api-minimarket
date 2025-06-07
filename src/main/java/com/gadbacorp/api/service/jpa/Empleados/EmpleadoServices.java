package com.gadbacorp.api.service.jpa.Empleados;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.empleados.Empleado;
import com.gadbacorp.api.repository.empleados.EmpleadosRepository;
import com.gadbacorp.api.service.empleados.IEmpleadoServices;

@Service
public class EmpleadoServices implements IEmpleadoServices {

   @Autowired
   private EmpleadosRepository repoEmpleado;
   public List<Empleado> buscarTodos() {
      return repoEmpleado.findAll();
   }
   public void guardar(Empleado empleado) {
      repoEmpleado.save(empleado);
   }
   public void modificar(Empleado empleado) {
      repoEmpleado.save(empleado);
   }
   public Optional<Empleado> buscarId(Integer id) {
      return repoEmpleado.findById(id);
   }
    public void eliminar(Integer id) {
        repoEmpleado.deleteById(id);
    }

}

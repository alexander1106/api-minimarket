package com.gadbacorp.api.repository.empleados;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.empleados.Empleado;



public interface EmpleadosRepository extends JpaRepository<Empleado, Integer> {
        // Aquí puedes agregar métodos personalizados si es necesario
}

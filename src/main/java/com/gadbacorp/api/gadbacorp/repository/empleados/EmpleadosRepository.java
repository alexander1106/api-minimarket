package com.gadbacorp.api.gadbacorp.repository.empleados;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.gadbacorp.entity.empleados.Empleado;



public interface EmpleadosRepository extends JpaRepository<Empleado, Integer> {
        // Aquí puedes agregar métodos personalizados si es necesario
}

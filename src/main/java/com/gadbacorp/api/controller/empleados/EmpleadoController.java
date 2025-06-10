package com.gadbacorp.api.controller.empleados;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.gadbacorp.api.entity.empleados.Empleado;
import com.gadbacorp.api.service.empleados.IEmpleadoServices;

@RestController
@RequestMapping("/api/minimarket")
public class EmpleadoController {

    
   
    @Autowired
    private IEmpleadoServices empleadoService;

    @GetMapping("/empleados")
    public List<Empleado> buscarTodos() {
        return empleadoService.buscarTodos();
    }
    @PostMapping("/empleados")
    public Empleado guardar(@RequestBody Empleado empleado) {
        empleadoService.guardar(empleado);        
        return empleado;
    }
    
    @PutMapping("/empleados") //La diferencia acá es que subiremos el id, no ponemos "/clientes/id" por temas de seguridad, acuérdate de la caja de la unidad 2, eso no era correcto
    public Empleado modificar(@RequestBody Empleado empleado) {        
        empleadoService.modificar(empleado);
        return empleado;
    }

    @GetMapping("/empleados/{id}")
    public Optional<Empleado> buscarId(@PathVariable("id") Integer id){
        return empleadoService.buscarId(id);
    }

    @DeleteMapping("/empleados/{id}")
    public String eliminar(@PathVariable Integer id){
        empleadoService.eliminar(id);
        return "Sucursal elimnado";
    }
    
}

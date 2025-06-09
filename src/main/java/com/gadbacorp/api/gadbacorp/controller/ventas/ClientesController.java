package com.gadbacorp.api.gadbacorp.controller.ventas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.gadbacorp.entity.ventas.Clientes;
import com.gadbacorp.api.gadbacorp.service.ventas.IClientesService;
@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin("*")
public class ClientesController {
    
    @Autowired
    private IClientesService clientesService;

    @GetMapping("/clientes")
    public List<Clientes> buscarTodos() {
        return clientesService.obetenrTodosClientes();
    }

   @PostMapping("/cliente")
    public Clientes guadarCliente(@RequestBody Clientes cliente) {
        return clientesService.crearCliente(cliente);
    }

    @PutMapping("/cliente")
    public Clientes modificar(@RequestBody Clientes metodoPago) {
        clientesService.crearCliente(metodoPago);
        return metodoPago;
    }

    @GetMapping("/cliente/{id}")
    public Optional<Clientes> buscarId(@PathVariable("id") Integer id){
        return clientesService.obtenerCliente(id);
    }
    @DeleteMapping("/cliente/{id}")
    public String eliminar(@PathVariable Integer id){
        clientesService.eliminarCliente(id);
        return "El cliente fue eliminado";
    }
}

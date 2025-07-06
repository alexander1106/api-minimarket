package com.gadbacorp.api.controller.ventas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.service.jpa.ventas.ApiCliente;
import com.gadbacorp.api.service.ventas.IClientesService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/minimarket")
public class ClientesController {

    @Autowired
    private IClientesService clientesService;

    @Autowired
    private ApiCliente apiCliente;

    // Consultar datos del cliente por API externa (RENIEC)
    @GetMapping("/clientes/reniec/{dni}")
    public String obtenerDatosReniec(@PathVariable String dni) {
        System.out.println("Se recibió el DNI: " + dni);
        return apiCliente.consumirApi(dni);
    }

    // Listar todos los clientes
    @GetMapping("/clientes")
    public List<Clientes> listarClientes() {
        return clientesService.obetenrTodosClientes();
    }

    // Listar clientes por sucursal
    @GetMapping("/clientes/sucursal/{idSucursal}")
    public List<Clientes> listarClientesPorSucursal(@PathVariable Integer idSucursal) {
        return clientesService.obtenerClientesPorSucursal(idSucursal);
    }

    // Guardar cliente
    @PostMapping("/clientes")
    public ResponseEntity<?> guardarCliente(@RequestBody Clientes cliente) {
        return ResponseEntity.ok(clientesService.crearCliente(cliente));
    }

    // Modificar cliente
    @PutMapping("/clientes")
    public Clientes modificar(@RequestBody Clientes cliente) {
        return clientesService.crearCliente(cliente);
    }

    // Buscar cliente por ID
    @GetMapping("/clientes/{id}")
    public Optional<Clientes> buscarId(@PathVariable("id") Integer id) {
        return clientesService.obtenerCliente(id);
    }

    // Eliminar cliente (borrado lógico)
    @DeleteMapping("/clientes/{id}")
    public String eliminar(@PathVariable Integer id) {
        clientesService.eliminarCliente(id);
        return "El cliente fue eliminado";
    }
}

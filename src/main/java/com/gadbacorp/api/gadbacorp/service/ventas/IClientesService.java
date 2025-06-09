package com.gadbacorp.api.gadbacorp.service.ventas;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.gadbacorp.entity.ventas.Clientes;

public interface IClientesService {
    List<Clientes> obetenrTodosClientes();
    Clientes editarCliente(Clientes clientes); 
    Optional<Clientes> obtenerCliente(Integer clienteId);
    Clientes crearCliente(Clientes cliente);
    void eliminarCliente(Integer idCliente);
}

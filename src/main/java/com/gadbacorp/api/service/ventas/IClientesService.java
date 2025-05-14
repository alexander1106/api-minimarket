package com.gadbacorp.api.service.ventas;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.ventas.Clientes;

public interface IClientesService {
    List<Clientes> obetenrTodosClientes();
    void editarCliente(Clientes clientes); 
    Optional<Clientes> obtenerCliente(Integer clienteId);
    void crearCliente(Clientes cliente);
    void eliminarCliente(Integer idCliente);
}

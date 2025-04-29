package com.gadbacorp.api.service.ventas;

import java.util.List;

import com.gadbacorp.api.entity.ventas.Clientes;

public interface IClientesService {
    List<Clientes> obetenrTodosClientes();
    Clientes editarCliente(Clientes clientes); 
    Clientes obtenerCliente(Integer clienteId);
    Clientes crearCliente(Clientes cliente);
    void eliminarCliente(Integer idCliente);
}

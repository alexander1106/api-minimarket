package com.gadbacorp.api.service.ventas;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.ventas.Clientes;

public interface IClientesService {
    List<Clientes> obetenrTodosClientes();
    Clientes editarCliente(Clientes clientes); 
    Optional<Clientes> obtenerCliente(Integer clienteId);
    Clientes crearCliente(Clientes cliente);
    void eliminarCliente(Integer idCliente);
    List<Clientes> buscarPorDocumento(String documento);
    List<Clientes> obtenerClientesPorSucursal(Integer idSucursal);

}

package com.gadbacorp.api.service.jpa.ventas;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.repository.ventas.ClientesRepository;
import com.gadbacorp.api.service.ventas.IClientesService;

public class ClientesService implements IClientesService{
    @Autowired
    private ClientesRepository clientesRepository;

    @Override
    public List<Clientes> obetenrTodosClientes() {
        return clientesRepository.findAll();
    }

    @Override
    public Clientes editarCliente(Clientes cliente) {
        return clientesRepository.save(cliente);
    }

    @Override
    public Clientes obtenerCliente(Integer clienteId) {
        return clientesRepository.findById(clienteId).orElse(null);
    }

    @Override
    public Clientes crearCliente(Clientes cliente) {
        return clientesRepository.save(cliente);
    }

    @Override
    public void eliminarCliente(Integer idCliente) {
        clientesRepository.deleteById(idCliente);
    }
}

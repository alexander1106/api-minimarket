package com.gadbacorp.api.service.jpa.ventas;
import java.util.List;
import java.util.Optional;

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
    public void editarCliente(Clientes cliente) {
        clientesRepository.save(cliente);
    }

    @Override
    public Optional obtenerCliente(Integer clienteId) {
        return clientesRepository.findById(clienteId);
    }

    @Override
    public void crearCliente(Clientes cliente) {
        clientesRepository.save(cliente);
    }

    @Override
    public void eliminarCliente(Integer idCliente) {
        clientesRepository.deleteById(idCliente);
    }
}

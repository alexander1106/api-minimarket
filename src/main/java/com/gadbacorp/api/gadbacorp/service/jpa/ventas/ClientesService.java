package com.gadbacorp.api.gadbacorp.service.jpa.ventas;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.gadbacorp.entity.ventas.Clientes;
import com.gadbacorp.api.gadbacorp.repository.ventas.ClientesRepository;
import com.gadbacorp.api.gadbacorp.service.ventas.IClientesService;
@Service
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
    public Optional obtenerCliente(Integer clienteId) {
        return clientesRepository.findById(clienteId);
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

package com.gadbacorp.api.service.jpa.ventas;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.repository.ventas.ClientesRepository;
import com.gadbacorp.api.service.ventas.IClientesService;
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
    public Optional<Clientes> obtenerCliente(Integer clienteId) {
        return clientesRepository.findById(clienteId);
    }

    @Override
    public Clientes crearCliente(Clientes cliente) {
        return clientesRepository.save(cliente);
    }
    @Override
    public void eliminarCliente(Integer idCliente) {
        try {
            clientesRepository.deleteById(idCliente);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("El cliente no puede ser eliminado porque tiene registros relacionados.");
        }
    }

    @Override
    public List<Clientes> buscarPorDocumento(String documento) {
        return clientesRepository.findByDocumento(documento);
    }
}

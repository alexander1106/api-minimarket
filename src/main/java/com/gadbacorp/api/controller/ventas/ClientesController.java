package com.gadbacorp.api.controller.ventas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.service.jpa.ventas.ApiCliente;
import com.gadbacorp.api.service.ventas.IClientesService;

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Ventas - Clientes")
@RestController
@RequestMapping("/api/minimarket")
public class ClientesController {

    @Autowired
    private IClientesService clientesService;

    @Autowired
    private ApiCliente apiCliente;

    @Autowired
    private com.gadbacorp.api.repository.ventas.ClientesRepository clientesRepository;

    @GetMapping("/clientes/reniec/{dni}")
    public org.springframework.http.ResponseEntity<String> obtenerDatosReniec(@PathVariable String dni) {
        // Se espera dentro de la petición: devolver un Mono desde MVC causa 403 en el despacho asíncrono
        try {
            String cuerpo = apiCliente.consumirApi(dni).block(java.time.Duration.ofSeconds(15));
            return org.springframework.http.ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(cuerpo == null ? "{}" : cuerpo);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.BAD_GATEWAY)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"No se pudo consultar a RENIEC.\"}");
        }
    }

    @GetMapping("/clientes")
    public List<Clientes> listarClientes() {
        return clientesService.obetenrTodosClientes();
    }

    @GetMapping("/clientes/sucursal/{idSucursal}")
    public List<Clientes> listarClientesPorSucursal(@PathVariable Integer idSucursal) {
        return clientesService.obtenerClientesPorSucursal(idSucursal);
    }

@PostMapping("/clientes")
public ResponseEntity<Map<String, Object>> guardarCliente(@RequestBody Clientes cliente) {
    validar(cliente);
    clientesService.crearCliente(cliente);
    
    Map<String, Object> respuesta = new HashMap<>();
    respuesta.put("status", 200);
    respuesta.put("Detalle", "Cliente guardado");
    
    return ResponseEntity.ok(respuesta);
}

    @PutMapping("/clientes")
    public Clientes modificar(@RequestBody Clientes cliente) {
        validar(cliente);
        return clientesService.crearCliente(cliente);
    }

    @GetMapping("/clientes/{id}")
    public Optional<Clientes> buscarId(@PathVariable("id") Integer id) {
        return clientesService.obtenerCliente(id);
    }   

   @DeleteMapping("/clientes/{id}")
public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Integer id) {
    clientesService.eliminarCliente(id);
    
    Map<String, Object> respuesta = new HashMap<>();
    respuesta.put("status", 200);
    respuesta.put("Detalle", "El cliente fue eliminado");
    
    return ResponseEntity.ok(respuesta);
}

    /** DNI de 8 digitos, RUC de 11 y documento unico dentro de la misma sucursal. */
    private void validar(Clientes cliente) {
        String doc = cliente.getDocumento() == null ? "" : cliente.getDocumento().trim();
        String tipo = cliente.getTipoDocumento() == null ? "" : cliente.getTipoDocumento().trim().toUpperCase();
        if (cliente.getNombre() == null || cliente.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del cliente es obligatorio.");
        }
        if ("DNI".equals(tipo) && !doc.matches("\\d{8}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El DNI debe tener 8 dígitos.");
        }
        if ("RUC".equals(tipo) && !doc.matches("\\d{11}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El RUC debe tener 11 dígitos.");
        }
        if (!doc.isEmpty()) {
            Integer sucursal = cliente.getSucursal() != null ? cliente.getSucursal().getIdSucursal() : null;
            boolean duplicado = clientesRepository.findByDocumento(doc).stream()
                    .filter(c -> !c.getIdCliente().equals(cliente.getIdCliente()))
                    .anyMatch(c -> sucursal == null || c.getSucursal() == null
                            || sucursal.equals(c.getSucursal().getIdSucursal()));
            if (duplicado) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un cliente con ese documento.");
            }
        }
    }
}

package  com.gadbacorp.api.controller.delivery;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.delivery.Vehiculo;
import com.gadbacorp.api.service.delivery.IVehiculoService;

@RestController
@RequestMapping("/api/minimarket")
public class VehiculosController {
    @Autowired
    private IVehiculoService service;

    @GetMapping("/vehiculo")
    public List<Vehiculo> listar() {
        return service.buscarTodos();
    }

    @GetMapping("/vehiculo/{id}")
    public Optional<Vehiculo> buscar(@PathVariable Integer id) {
        return service.buscarId(id);
    }

    @PostMapping("/vehiculo")
    public Vehiculo guardar(@RequestBody Vehiculo vehiculo) {
        return service.guardar(vehiculo);
    }

    @PutMapping("/vehiculo")
    public Vehiculo modificar(@RequestBody Vehiculo vehiculo) {
        return service.modificar(vehiculo);
    }

    @DeleteMapping("/vehiculo/{id}")
    public String eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return "Vehiculo eliminado";
    }
}


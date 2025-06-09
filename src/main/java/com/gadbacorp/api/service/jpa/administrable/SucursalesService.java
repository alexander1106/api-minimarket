package com.gadbacorp.api.service.jpa.administrable;

import com.gadbacorp.api.entity.administrable.EmpresaDTO;
import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.AlmacenesDTO;
import com.gadbacorp.api.entity.administrable.SucursalDTO;

import com.gadbacorp.api.repository.administrable.SucursalesRepository;
import com.gadbacorp.api.service.administrable.ISucursalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SucursalesService implements ISucursalesService {
    
    private final SucursalesRepository repoSucursales;

    @Autowired
    public SucursalesService(SucursalesRepository repoSucursales) {
        this.repoSucursales = repoSucursales;
    }

    @Override
    public List<SucursalDTO> buscarTodosDTO() {
        return repoSucursales.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SucursalDTO> buscarIdDTO(Integer id) {
        return repoSucursales.findById(id)
                .map(this::convertToDTO);
    }

    private SucursalDTO convertToDTO(Sucursales sucursal) {
        SucursalDTO dto = new SucursalDTO();
        dto.setIdSucursal(sucursal.getIdSucursal());
        dto.setNombreSucursal(sucursal.getNombreSucursal());
        dto.setContacto(sucursal.getContacto());
        dto.setDireccion(sucursal.getDireccion());

        if (sucursal.getEmpresa() != null) {
        EmpresaDTO empresaDTO = new EmpresaDTO();
        empresaDTO.setIdEmpresa(sucursal.getEmpresa().getIdempresa());
        empresaDTO.setRazonSocial(sucursal.getEmpresa().getRazonsocial());
        empresaDTO.setCiudad(sucursal.getEmpresa().getCiudad());
        empresaDTO.setDireccion(sucursal.getEmpresa().getDireccion());
        empresaDTO.setCantidadSucursales(sucursal.getEmpresa().getCant_sucursales());
        empresaDTO.setCantidadTrabajadores(sucursal.getEmpresa().getCant_trabajadores());
        empresaDTO.setLimiteInventario(sucursal.getEmpresa().getLimit_inventario());
        empresaDTO.setCorreo(sucursal.getEmpresa().getCorreo());

    // Convertir el Blob logo a byte[] si no es null
    try {
        if (sucursal.getEmpresa().getLogo() != null) {
            int length = (int) sucursal.getEmpresa().getLogo().length();
            byte[] logoBytes = sucursal.getEmpresa().getLogo().getBytes(1, length);
            empresaDTO.setLogo(logoBytes);
        }
    } catch (Exception e) {
        e.printStackTrace(); // o usa un logger si tienes uno configurado
    }

    dto.setEmpresa(empresaDTO);
}



        if (sucursal.getAlmacenes() != null && !sucursal.getAlmacenes().isEmpty()) {
            List<AlmacenesDTO> almacenesDTO = sucursal.getAlmacenes().stream()
                    .map(this::convertAlmacenToDTO)
                    .collect(Collectors.toList());
            dto.setAlmacenes(almacenesDTO);
        }

        return dto;
    }

    private AlmacenesDTO convertAlmacenToDTO(Almacenes almacen) {
        AlmacenesDTO dto = new AlmacenesDTO();
        dto.setIdalmacen(almacen.getIdalmacen());
        dto.setNombre(almacen.getNombre());
        dto.setDescripcion(almacen.getDescripcion());
        dto.setEstado(almacen.getEstado());
        dto.setIdSucursal(almacen.getSucursal().getIdSucursal());
        return dto;
    }

    // Resto de los métodos existentes...
    @Override
    public List<Sucursales> buscarTodos() {
        return repoSucursales.findAll();
    }

    @Override
    public void guardar(Sucursales sucursal) {
        repoSucursales.save(sucursal);
    }

    @Override
    public void modificar(Sucursales sucursal) {
        repoSucursales.save(sucursal);
    }

    @Override
    public Optional<Sucursales> buscarId(Integer id) {
        return repoSucursales.findById(id);
    }

    @Override
    public void eliminar(Integer id) {
        repoSucursales.deleteById(id);
    }
}

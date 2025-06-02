package com.gadbacorp.api.service.jpa.administrable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.dto.administrable.AlmacenDTO;
import com.gadbacorp.api.entity.dto.administrable.SucursalDTO;
import com.gadbacorp.api.repository.administrable.SucursalesRepository;
import com.gadbacorp.api.service.administrable.ISucursalesService;

@Service
public class SucursalesService implements ISucursalesService {
    @Autowired
private SucursalesRepository repoSucursales;

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

// ✅ Devuelve lista de DTOs con empresa y almacenes
@Override
public List<SucursalDTO> buscarTodosDTO() {
    return repoSucursales.findAll().stream()
        .map(sucursal -> {
            SucursalDTO dto = new SucursalDTO();
            dto.setIdSucursal(sucursal.getIdSucursal());
            dto.setNombreSucursal(sucursal.getNombreSucursal());
            dto.setContacto(sucursal.getContacto());
            dto.setDireccion(sucursal.getDireccion());

            if (sucursal.getEmpresa() != null) {
                dto.setIdEmpresa(sucursal.getEmpresa().getIdempresa());
                dto.setRazonSocialEmpresa(sucursal.getEmpresa().getRazonsocial());
            }

            List<AlmacenDTO> almacenesDTO = sucursal.getAlmacenes().stream()
                .map(almacen -> new AlmacenDTO(
                    almacen.getIdalmacen(),
                    almacen.getNombre()
                ))
                .collect(Collectors.toList());

            dto.setAlmacenes(almacenesDTO);
            return dto;
        })
        .collect(Collectors.toList());
}

}
package com.gadbacorp.api.gadbacorp.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.gadbacorp.entity.inventario.PromocionesDTO;

public interface IPromocionesService {

    List<PromocionesDTO> buscarTodosDTO();

    Optional<PromocionesDTO> buscarIdDTO(Integer id);

    PromocionesDTO guardarDTO(PromocionesDTO dto);

    PromocionesDTO actualizarDTO(Integer id, PromocionesDTO dto);

    void eliminar(Integer id);
}

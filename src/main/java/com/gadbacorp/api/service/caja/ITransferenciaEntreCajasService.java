package com.gadbacorp.api.service.caja;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.caja.TransferenciasEntreCajas;

public interface  ITransferenciaEntreCajasService {
    TransferenciasEntreCajas guardarTransferenciasEntreCajas(TransferenciasEntreCajas transferenciasEntreCajas);
    TransferenciasEntreCajas editarTransferenciasEntreCajas(TransferenciasEntreCajas transferenciasEntreCajas);
    List<TransferenciasEntreCajas> listarTransferenciasEntreCajas();
    Optional<TransferenciasEntreCajas> buscarTranseferenciaEntreCajas(Integer id); 
    void eliminarTransferenciaEntreCajas(Integer id);
}

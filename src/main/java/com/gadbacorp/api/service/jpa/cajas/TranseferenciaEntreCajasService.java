package com.gadbacorp.api.service.jpa.cajas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.caja.TransferenciasEntreCajas;
import com.gadbacorp.api.repository.caja.TransferenciaEntreCajasRepository;
import com.gadbacorp.api.service.caja.ITransferenciaEntreCajasService;
@Service
public class TranseferenciaEntreCajasService implements  ITransferenciaEntreCajasService{
@Autowired
private TransferenciaEntreCajasRepository transferenciaEntreCajasRepository;
    @Override
    public TransferenciasEntreCajas guardarTransferenciasEntreCajas(TransferenciasEntreCajas transferenciasEntreCajas) {
        return transferenciaEntreCajasRepository.save(transferenciasEntreCajas);
    }

    @Override
    public TransferenciasEntreCajas editarTransferenciasEntreCajas(TransferenciasEntreCajas transferenciasEntreCajas) {
        return transferenciaEntreCajasRepository.save(transferenciasEntreCajas);
    }

    @Override
    public List<TransferenciasEntreCajas> listarTransferenciasEntreCajas() {
        return transferenciaEntreCajasRepository.findAll();
    }

    @Override
    public Optional<TransferenciasEntreCajas> buscarTranseferenciaEntreCajas(Integer id) {
        return transferenciaEntreCajasRepository.findById(id);
    }

    @Override
    public void eliminarTransferenciaEntreCajas(Integer id) {
        transferenciaEntreCajasRepository.deleteById(id);
    }
}

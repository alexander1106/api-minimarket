package com.gadbacorp.api.host;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.repository.ventas.VentasRepository;

/**
 * Archivo batch de ventas para el Host: cabecera (H) + un registro por venta (D) + totales de control (T).
 * Igual que en un proceso nocturno de mainframe, el receptor verifica los totales de control antes de procesar.
 */
@Service
public class HostVentasService {

    static final String ORIGEN = "MINIMARKET";

    private final VentasRepository ventasRepository;

    public HostVentasService(VentasRepository ventasRepository) {
        this.ventasRepository = ventasRepository;
    }

    /** Resultado de leer un archivo del Host. */
    public record Lectura(RegistroHost.Cabecera cabecera, List<RegistroHost.Venta> ventas, RegistroHost.Totales totales) {
    }

    @Transactional(readOnly = true)
    public byte[] exportar(LocalDate fecha, Integer idSucursal) {
        List<RegistroHost.Venta> registros = new ArrayList<>();
        for (Ventas v : ventasRepository.listarPorFecha(fecha)) {
            if (idSucursal != null && (v.getCliente() == null || v.getCliente().getSucursal() == null
                    || !idSucursal.equals(v.getCliente().getSucursal().getIdSucursal()))) {
                continue;
            }
            if ("Eliminada".equalsIgnoreCase(v.getEstado_venta())) {
                continue;
            }
            // cada empresa exporta solo sus propias ventas
            Integer empresa = v.getCliente() == null || v.getCliente().getSucursal() == null ? null
                    : com.gadbacorp.api.tenant.TenantService.empresaDe(v.getCliente().getSucursal());
            if (!com.gadbacorp.api.tenant.TenantService.puedeVer(empresa)) {
                continue;
            }
            registros.add(aRegistro(v));
        }
        return generar(fecha, registros);
    }

    /** Arma el archivo a partir de registros ya construidos (separado para poder probarlo sin base de datos). */
    public byte[] generar(LocalDate fecha, List<RegistroHost.Venta> ventas) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        salida.writeBytes(RegistroHost.cabecera(new RegistroHost.Cabecera(fecha, ORIGEN)));
        BigDecimal total = BigDecimal.ZERO;
        for (RegistroHost.Venta v : ventas) {
            salida.writeBytes(RegistroHost.venta(v));
            total = total.add(v.total());
        }
        salida.writeBytes(RegistroHost.totales(new RegistroHost.Totales(ventas.size(), total)));
        return salida.toByteArray();
    }

    /**
     * Lee y valida un archivo: estructura H, D..., T, longitud multiplo de 60 y totales de control iguales
     * a lo realmente recibido.
     *
     * @throws IllegalArgumentException si el archivo esta danado o los totales no cuadran
     */
    public Lectura leer(byte[] archivo) {
        if (archivo == null || archivo.length == 0 || archivo.length % RegistroHost.LONGITUD != 0) {
            throw new IllegalArgumentException("Archivo invalido: el tamano debe ser multiplo de "
                    + RegistroHost.LONGITUD + " bytes");
        }
        int cantidadRegistros = archivo.length / RegistroHost.LONGITUD;
        if (cantidadRegistros < 2) {
            throw new IllegalArgumentException("Archivo invalido: faltan la cabecera o los totales");
        }

        RegistroHost.Cabecera cabecera = RegistroHost.leerCabecera(registro(archivo, 0));
        List<RegistroHost.Venta> ventas = new ArrayList<>();
        BigDecimal suma = BigDecimal.ZERO;
        for (int i = 1; i < cantidadRegistros - 1; i++) {
            RegistroHost.Venta v = RegistroHost.leerVenta(registro(archivo, i));
            ventas.add(v);
            suma = suma.add(v.total());
        }
        RegistroHost.Totales totales = RegistroHost.leerTotales(registro(archivo, cantidadRegistros - 1));

        if (totales.cantidad() != ventas.size()) {
            throw new IllegalArgumentException("Totales de control no cuadran: el archivo declara "
                    + totales.cantidad() + " ventas y trae " + ventas.size());
        }
        if (totales.total().compareTo(suma) != 0) {
            throw new IllegalArgumentException("Totales de control no cuadran: el archivo declara un total de "
                    + totales.total() + " y la suma es " + suma);
        }
        return new Lectura(cabecera, ventas, totales);
    }

    private static byte[] registro(byte[] archivo, int indice) {
        byte[] r = new byte[RegistroHost.LONGITUD];
        System.arraycopy(archivo, indice * RegistroHost.LONGITUD, r, 0, RegistroHost.LONGITUD);
        return r;
    }

    private static RegistroHost.Venta aRegistro(Ventas v) {
        boolean factura = "FACTURA".equalsIgnoreCase(v.getTipo_comprobante());
        String doc = v.getCliente() != null && v.getCliente().getDocumento() != null ? v.getCliente().getDocumento() : "";
        return new RegistroHost.Venta(
                v.getIdVenta(),
                factura ? "F" : "B",
                v.getNro_comrprobante() == null ? "" : v.getNro_comrprobante(),
                v.getFecha_venta(),
                doc,
                BigDecimal.valueOf(v.getTotal_venta() == null ? 0.0 : v.getTotal_venta()),
                v.getEstado_venta() == null ? "" : v.getEstado_venta());
    }
}

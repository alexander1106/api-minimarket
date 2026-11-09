package com.gadbacorp.api.service.jpa.compras;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.entity.compras.ProveedorDTO;
import com.gadbacorp.api.entity.compras.Proveedores;
import com.gadbacorp.api.excepciones.OperacionNoPermitidaException;
import com.gadbacorp.api.excepciones.RecursoDuplicadoException;
import com.gadbacorp.api.excepciones.RegistroInvalidoException;
import com.gadbacorp.api.repository.administrable.EmpresasRepository;
import com.gadbacorp.api.repository.compras.ComprasRepository;
import com.gadbacorp.api.repository.compras.ProveedoresRepository;

/** Alta, edicion y baja de proveedores con sus reglas: datos validos, RUC unico por empresa y sin borrar los que tienen compras. */
@Service
public class ProveedorRegistroService {

    private final ProveedoresRepository proveedoresRepository;
    private final EmpresasRepository empresasRepository;
    private final ComprasRepository comprasRepository;

    public ProveedorRegistroService(ProveedoresRepository proveedoresRepository,
                                    EmpresasRepository empresasRepository,
                                    ComprasRepository comprasRepository) {
        this.proveedoresRepository = proveedoresRepository;
        this.empresasRepository = empresasRepository;
        this.comprasRepository = comprasRepository;
    }

    @Transactional
    public Proveedores crear(ProveedorDTO dto) {
        Map<String, String> errores = validar(dto);
        Empresas empresa = null;
        if (dto.getIdEmpresa() == null) {
            errores.put("idEmpresa", "La empresa es obligatoria.");
        } else {
            empresa = empresasRepository.findById(dto.getIdEmpresa()).orElse(null);
            if (empresa == null) {
                errores.put("idEmpresa", "La empresa no existe.");
            }
        }
        if (!errores.isEmpty()) {
            throw new RegistroInvalidoException(errores);
        }
        String ruc = dto.getRuc().trim();
        if (proveedoresRepository.existsByRucAndEmpresaIdempresa(ruc, empresa.getIdempresa())) {
            throw new RecursoDuplicadoException("Ya existe un proveedor con el RUC " + ruc + " en esta empresa.");
        }

        Proveedores p = new Proveedores();
        aplicar(p, dto);
        p.setFecha_registro(new java.sql.Date(System.currentTimeMillis())); // la fecha de alta la pone el servidor
        p.setEstado(1);                    // un proveedor nuevo siempre nace activo (estado=0 equivale a eliminado)
        p.setEmpresa(empresa);
        return proveedoresRepository.save(p);
    }

    @Transactional
    public Proveedores actualizar(ProveedorDTO dto) {
        if (dto.getIdProveedor() == null) {
            throw new RegistroInvalidoException(Map.of("idProveedor", "El id del proveedor es obligatorio."));
        }
        Proveedores p = proveedoresRepository.findById(dto.getIdProveedor())
                .orElseThrow(() -> new NoSuchElementException("Proveedor no encontrado con ID: " + dto.getIdProveedor()));
        Map<String, String> errores = validar(dto);
        if (!errores.isEmpty()) {
            throw new RegistroInvalidoException(errores);
        }
        Integer idEmpresaActual = p.getEmpresa() != null ? p.getEmpresa().getIdempresa() : null;
        if (dto.getIdEmpresa() != null && idEmpresaActual != null && !dto.getIdEmpresa().equals(idEmpresaActual)) {
            throw new OperacionNoPermitidaException("Un proveedor no se puede mover a otra empresa.");
        }
        String ruc = dto.getRuc().trim();
        if (idEmpresaActual != null && proveedoresRepository
                .existsByRucAndEmpresaIdempresaAndIdProveedorNot(ruc, idEmpresaActual, p.getIdProveedor())) {
            throw new RecursoDuplicadoException("Ya existe otro proveedor con el RUC " + ruc + " en esta empresa.");
        }
        aplicar(p, dto); // conserva fecha de registro, estado y empresa
        return proveedoresRepository.save(p);
    }

    @Transactional
    public void eliminar(Integer id) {
        Proveedores p = proveedoresRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Proveedor no encontrado con ID: " + id));
        int compras = comprasRepository.findByProveedorIdProveedor(id).size();
        if (compras > 0) {
            throw new OperacionNoPermitidaException("No se puede eliminar el proveedor " + p.getNombre()
                    + ": tiene " + compras + " compra(s) registradas.");
        }
        proveedoresRepository.deleteById(id);
    }

    static Map<String, String> validar(ProveedorDTO d) {
        Map<String, String> errores = new LinkedHashMap<>();
        if (vacio(d.getNombre())) {
            errores.put("nombre", "El nombre del proveedor es obligatorio.");
        } else if (d.getNombre().trim().length() > 150) {
            errores.put("nombre", "El nombre no puede pasar de 150 caracteres.");
        }
        if (vacio(d.getRuc()) || !d.getRuc().trim().matches("(10|15|16|17|20)\\d{9}")) {
            errores.put("ruc", "El RUC debe tener 11 dígitos y empezar con 10, 15, 16, 17 o 20.");
        }
        if (!vacio(d.getGmail()) && !d.getGmail().trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            errores.put("gmail", "El correo electrónico no es válido.");
        }
        if (!vacio(d.getTelefono()) && d.getTelefono().replaceAll("\\D", "").length() < 9) {
            errores.put("telefono", "El teléfono debe tener al menos 9 dígitos.");
        }
        validarFicha(d, errores);
        return errores;
    }

    /** Reglas de la ficha ampliada: todos los campos son opcionales, pero si vienen deben ser coherentes. */
    private static void validarFicha(ProveedorDTO d, Map<String, String> errores) {
        limite(d.getRazonSocial(), 200, "razonSocial", errores);
        limite(d.getContactoNombre(), 120, "contactoNombre", errores);
        limite(d.getContactoCargo(), 80, "contactoCargo", errores);
        limite(d.getRubro(), 120, "rubro", errores);
        limite(d.getBanco(), 80, "banco", errores);
        limite(d.getNotas(), 1000, "notas", errores);
        if (!vacio(d.getTelefonoSecundario()) && d.getTelefonoSecundario().replaceAll("\\D", "").length() < 9) {
            errores.put("telefonoSecundario", "El teléfono secundario debe tener al menos 9 dígitos.");
        }
        if (!vacio(d.getSitioWeb()) && !d.getSitioWeb().trim().matches("(?i)^(https?://)?([\\w-]+\\.)+[a-z]{2,}(/\\S*)?$")) {
            errores.put("sitioWeb", "El sitio web no es válido (ej. www.proveedor.pe).");
        }
        if (!vacio(d.getCci()) && !d.getCci().trim().matches("\\d{20}")) {
            errores.put("cci", "El CCI debe tener 20 dígitos.");
        }
        if (!vacio(d.getNumeroCuenta()) && !d.getNumeroCuenta().trim().matches("[\\d-]{8,30}")) {
            errores.put("numeroCuenta", "El número de cuenta solo admite dígitos y guiones (8 a 30 caracteres).");
        }
        String condicion = normalizarCondicion(d.getCondicionPago());
        if (condicion == null) {
            errores.put("condicionPago", "La condición de pago debe ser CONTADO o CREDITO.");
        } else if ("CREDITO".equals(condicion)) {
            if (d.getDiasCredito() == null || d.getDiasCredito() < 1 || d.getDiasCredito() > 365) {
                errores.put("diasCredito", "Indica los días de crédito (de 1 a 365).");
            }
        }
        if (d.getMoneda() != null && !d.getMoneda().isBlank()
                && !d.getMoneda().trim().toUpperCase().matches("PEN|USD")) {
            errores.put("moneda", "La moneda debe ser PEN o USD.");
        }
        if (d.getLimiteCredito() != null && d.getLimiteCredito().signum() < 0) {
            errores.put("limiteCredito", "El límite de crédito no puede ser negativo.");
        }
    }

    private static void limite(String valor, int max, String campo, Map<String, String> errores) {
        if (valor != null && valor.trim().length() > max) {
            errores.put(campo, "No puede pasar de " + max + " caracteres.");
        }
    }

    /** CONTADO por defecto; null si el valor no es reconocido. */
    static String normalizarCondicion(String c) {
        if (c == null || c.isBlank()) {
            return "CONTADO";
        }
        String s = c.trim().toUpperCase();
        return s.equals("CONTADO") || s.equals("CREDITO") ? s : null;
    }

    private static void aplicar(Proveedores p, ProveedorDTO d) {
        p.setNombre(d.getNombre().trim());
        p.setRuc(d.getRuc().trim());
        p.setRegimen(limpiar(d.getRegimen()));
        p.setTelefono(limpiar(d.getTelefono()));
        p.setGmail(limpiar(d.getGmail()));
        p.setDireccion(limpiar(d.getDireccion()));

        p.setRazonSocial(limpiar(d.getRazonSocial()));
        p.setDistrito(limpiar(d.getDistrito()));
        p.setProvincia(limpiar(d.getProvincia()));
        p.setDepartamento(limpiar(d.getDepartamento()));
        p.setContactoNombre(limpiar(d.getContactoNombre()));
        p.setContactoCargo(limpiar(d.getContactoCargo()));
        p.setTelefonoSecundario(limpiar(d.getTelefonoSecundario()));
        p.setSitioWeb(limpiar(d.getSitioWeb()));
        p.setRubro(limpiar(d.getRubro()));
        String condicion = normalizarCondicion(d.getCondicionPago());
        p.setCondicionPago(condicion);
        p.setDiasCredito("CREDITO".equals(condicion) ? d.getDiasCredito() : null);
        p.setMoneda(vacio(d.getMoneda()) ? "PEN" : d.getMoneda().trim().toUpperCase());
        p.setLimiteCredito(d.getLimiteCredito());
        p.setBanco(limpiar(d.getBanco()));
        p.setNumeroCuenta(limpiar(d.getNumeroCuenta()));
        p.setCci(limpiar(d.getCci()));
        p.setEstadoSunat(limpiar(d.getEstadoSunat()));
        p.setCondicionSunat(limpiar(d.getCondicionSunat()));
        p.setNotas(limpiar(d.getNotas()));
    }

    private static String limpiar(String s) {
        return vacio(s) ? null : s.trim();
    }

    private static boolean vacio(String s) {
        return s == null || s.isBlank();
    }
}

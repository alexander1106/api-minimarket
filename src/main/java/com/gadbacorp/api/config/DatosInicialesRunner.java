package com.gadbacorp.api.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.entity.administrable.Plan;
import com.gadbacorp.api.entity.inventario.UnidadDeMedida;
import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.caja.Caja;
import com.gadbacorp.api.entity.empleados.Usuarios;
import com.gadbacorp.api.entity.seguridad.Modulo;
import com.gadbacorp.api.entity.seguridad.Rol;
import com.gadbacorp.api.entity.seguridad.RolModulo;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.repository.administrable.EmpresasRepository;
import com.gadbacorp.api.repository.administrable.PlanRepository;
import com.gadbacorp.api.repository.inventario.UnidadDeMedidaRepository;
import com.gadbacorp.api.repository.administrable.SucursalesRepository;
import com.gadbacorp.api.repository.caja.CajaRepository;
import com.gadbacorp.api.repository.empleados.UsuarioRepository;
import com.gadbacorp.api.repository.seguridad.ModuloRepository;
import com.gadbacorp.api.repository.seguridad.RolModuloRepository;
import com.gadbacorp.api.repository.seguridad.RolRepository;
import com.gadbacorp.api.repository.ventas.MetodosPagoRepository;

/**
 * Carga los datos minimos para poder entrar al sistema la primera vez: roles, modulos (los del menu del
 * frontend), permisos rol-modulo y un usuario administrador. Es idempotente: solo crea lo que falta,
 * nunca modifica ni borra datos existentes. Se desactiva con APP_SEED_ENABLED=false.
 */
@Component
@org.springframework.core.annotation.Order(1)
public class DatosInicialesRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosInicialesRunner.class);

    static final String SUPERADMIN = "SUPERADMIN";
    static final String ADMIN = "ADMIN";
    static final String SUPERVISOR = "SUPERVISOR";
    static final String CAJERO = "CAJERO";
    static final String ALMACENERO = "ALMACENERO";
    static final String EMPRESA = "EMPRESA";

    /**
     * Modulo del menu -> roles que lo ven. SUPERADMIN ve Administrable y Seguridad (usuarios); EMPRESA ve todo lo operativo.
     */
    static final Map<String, List<String>> MODULOS = new LinkedHashMap<>();
    static {
        MODULOS.put("Administrable", List.of(SUPERADMIN));
        MODULOS.put("Dashboard", List.of(ADMIN, SUPERVISOR, EMPRESA));
        MODULOS.put("Seguridad", List.of(ADMIN, EMPRESA, SUPERADMIN));
        MODULOS.put("Inventario", List.of(ADMIN, ALMACENERO, EMPRESA));
        MODULOS.put("Almacenes", List.of(ADMIN, ALMACENERO, EMPRESA));
        MODULOS.put("Productos", List.of(ADMIN, ALMACENERO, EMPRESA));
        MODULOS.put("Compras", List.of(ADMIN, ALMACENERO, EMPRESA));
        MODULOS.put("Ventas", List.of(ADMIN, CAJERO, EMPRESA));
        MODULOS.put("Delivery", List.of(ADMIN, SUPERVISOR, CAJERO, EMPRESA));
        MODULOS.put("Caja", List.of(ADMIN, CAJERO, EMPRESA));
        MODULOS.put("Reportes", List.of(ADMIN, CAJERO, EMPRESA));
    }

    private final RolRepository rolRepository;
    private final ModuloRepository moduloRepository;
    private final RolModuloRepository rolModuloRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresasRepository empresasRepository;
    private final SucursalesRepository sucursalesRepository;
    private final MetodosPagoRepository metodosPagoRepository;
    private final CajaRepository cajaRepository;
    private final PlanRepository planRepository;
    private final UnidadDeMedidaRepository unidadMedidaRepository;
    private final PasswordEncoder passwordEncoder;

    private final boolean habilitado;
    private final String adminUsername;
    private final String adminPassword;
    private final String empresaUsername;
    private final String empresaPassword;

    public DatosInicialesRunner(RolRepository rolRepository,
                                ModuloRepository moduloRepository,
                                RolModuloRepository rolModuloRepository,
                                UsuarioRepository usuarioRepository,
                                EmpresasRepository empresasRepository,
                                SucursalesRepository sucursalesRepository,
                                MetodosPagoRepository metodosPagoRepository,
                                CajaRepository cajaRepository,
                                PlanRepository planRepository,
                                UnidadDeMedidaRepository unidadMedidaRepository,
                                PasswordEncoder passwordEncoder,
                                @Value("${app.seed.enabled:true}") boolean habilitado,
                                @Value("${app.seed.admin-username:alez}") String adminUsername,
                                @Value("${app.seed.admin-password:1234}") String adminPassword,
                                @Value("${app.seed.empresa-username:empresa}") String empresaUsername,
                                @Value("${app.seed.empresa-password:1234}") String empresaPassword) {
        this.rolRepository = rolRepository;
        this.moduloRepository = moduloRepository;
        this.rolModuloRepository = rolModuloRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresasRepository = empresasRepository;
        this.sucursalesRepository = sucursalesRepository;
        this.metodosPagoRepository = metodosPagoRepository;
        this.cajaRepository = cajaRepository;
        this.planRepository = planRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.passwordEncoder = passwordEncoder;
        this.habilitado = habilitado;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.empresaUsername = empresaUsername;
        this.empresaPassword = empresaPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!habilitado) {
            return;
        }
        Map<String, Rol> roles = asegurarRoles();
        Map<String, Modulo> modulos = asegurarModulos();
        asegurarPermisos(roles, modulos);
        asegurarPlanes();
        asegurarUnidades();
        asegurarAdministrador(roles.get(SUPERADMIN));
        asegurarUsuarioEmpresa(roles.get(EMPRESA));
    }

    private Map<String, Rol> asegurarRoles() {
        Map<String, Rol> roles = new HashMap<>();
        for (String nombre : List.of(SUPERADMIN, ADMIN, SUPERVISOR, CAJERO, ALMACENERO, EMPRESA)) {
            Rol rol = rolRepository.findByNombre(nombre).orElseGet(() -> {
                Rol nuevo = new Rol();
                nuevo.setNombre(nombre);
                nuevo.setEstado(1);
                log.info("Seed: rol {}", nombre);
                return rolRepository.save(nuevo);
            });
            roles.put(nombre, rol);
        }
        return roles;
    }

    private Map<String, Modulo> asegurarModulos() {
        Map<String, Modulo> existentes = new HashMap<>();
        for (Modulo m : moduloRepository.findAll()) {
            existentes.put(m.getNombre(), m);
        }
        for (String nombre : MODULOS.keySet()) {
            existentes.computeIfAbsent(nombre, n -> {
                Modulo nuevo = new Modulo();
                nuevo.setNombre(n);
                nuevo.setEstado(1);
                log.info("Seed: modulo {}", n);
                return moduloRepository.save(nuevo);
            });
        }
        return existentes;
    }

    /** Planes de la plataforma. Solo se crean los que faltan por nombre; los precios se editan desde /planes. */
    private void asegurarPlanes() {
        Object[][] planes = {
                {"Gratis", "0.00", 1, 1},
                {"Básico", "79.00", 3, 1},
                {"Profesional", "169.00", 10, 3},
                {"Empresarial", "349.00", 25, 10},
        };
        for (Object[] p : planes) {
            String nombre = (String) p[0];
            if (planRepository.findByNombre(nombre).isEmpty()) {
                planRepository.save(new Plan(nombre, new BigDecimal((String) p[1]), (Integer) p[2], (Integer) p[3]));
                log.info("Seed: plan {}", nombre);
            }
        }
    }

    /** Unidades de medida base. Solo las que admiten decimales permiten vender 2.5 kg o 0.75 L. */
    private void asegurarUnidades() {
        Object[][] unidades = {
                {"Unidad", "UND", false},
                {"Kilogramo", "KG", true},
                {"Litro", "LT", true},
                {"Metro", "MT", true},
        };
        java.util.Set<String> existentes = new java.util.HashSet<>();
        for (UnidadDeMedida u : unidadMedidaRepository.findAll()) {
            existentes.add(u.getNombre() == null ? "" : u.getNombre().trim().toLowerCase());
        }
        for (Object[] d : unidades) {
            if (!existentes.contains(((String) d[0]).toLowerCase())) {
                UnidadDeMedida u = new UnidadDeMedida();
                u.setNombre((String) d[0]);
                u.setCodigo((String) d[1]);
                u.setPermiteDecimales((Boolean) d[2]);
                unidadMedidaRepository.save(u);
                log.info("Seed: unidad de medida {}", d[0]);
            }
        }
    }

    private void asegurarPermisos(Map<String, Rol> roles, Map<String, Modulo> modulos) {
        Set<String> yaAsignados = new HashSet<>();
        for (RolModulo rm : rolModuloRepository.findAll()) {
            if (rm.getRol() == null || rm.getModulo() == null) {
                continue;
            }
            // SUPERADMIN solo administra la plataforma (Administrable) y sus usuarios (Seguridad): se retiran
            // los permisos operativos que una carga anterior pudo haberle dado
            boolean superadminFueraDeAdministrable = SUPERADMIN.equals(rm.getRol().getNombre())
                    && MODULOS.containsKey(rm.getModulo().getNombre())
                    && !MODULOS.get(rm.getModulo().getNombre()).contains(SUPERADMIN);
            if (superadminFueraDeAdministrable) {
                rolModuloRepository.delete(rm);
                continue;
            }
            yaAsignados.add(rm.getRol().getId() + ":" + rm.getModulo().getIdModulo());
        }
        MODULOS.forEach((nombreModulo, nombresRol) -> {
            Modulo modulo = modulos.get(nombreModulo);
            for (String nombreRol : nombresRol) {
                Rol rol = roles.get(nombreRol);
                if (yaAsignados.add(rol.getId() + ":" + modulo.getIdModulo())) {
                    RolModulo permiso = new RolModulo();
                    permiso.setRol(rol);
                    permiso.setModulo(modulo);
                    permiso.setEstado(1);
                    rolModuloRepository.save(permiso);
                }
            }
        });
    }

    private void asegurarAdministrador(Rol superadmin) {
        Usuarios existente = usuarioRepository.findByUsername(adminUsername);
        if (existente != null) {
            // el dueno de la plataforma no pertenece a ninguna empresa: se desvincula si una carga anterior lo asigno
            if (existente.getSucursal() != null && existente.getRol() != null
                    && SUPERADMIN.equals(existente.getRol().getNombre())) {
                existente.setSucursal(null);
                usuarioRepository.save(existente);
                log.info("Seed: usuario '{}' desvinculado de su sucursal (SUPERADMIN no pertenece a una empresa)", adminUsername);
            }
            return;
        }

        Usuarios admin = new Usuarios();
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setNombre("Alez");
        admin.setApellidos("Administrador");
        admin.setEmail(adminUsername + "@minimarket.local");
        admin.setDni("00000000");
        admin.setTurno("MAÑANA");
        admin.setEstado(1);
        admin.setEnable(true);
        admin.setFechaCreacion(LocalDateTime.now());
        admin.setRol(superadmin);
        // sin sucursal: SUPERADMIN administra la plataforma, las sucursales pertenecen a las empresas
        usuarioRepository.save(admin);

        log.warn("Seed: usuario '{}' creado con rol SUPERADMIN. Cambia su contrasena ({}) antes de pasar a produccion.",
                adminUsername, "1234".equals(adminPassword) ? "es la de por defecto" : "definida por APP_SEED_ADMIN_PASSWORD");
    }

    /** Usuario con rol EMPRESA (acceso a todo lo operativo) en la primera sucursal, para poder probar el sistema. */
    private void asegurarUsuarioEmpresa(Rol rolEmpresa) {
        if (usuarioRepository.findByUsername(empresaUsername) != null) {
            return;
        }
        Sucursales sucursal = sucursalesRepository.findAll().stream().findFirst().orElseGet(this::crearEmpresaYSucursalBase);
        Usuarios u = new Usuarios();
        u.setUsername(empresaUsername);
        u.setPassword(passwordEncoder.encode(empresaPassword));
        u.setNombre("Empresa");
        u.setApellidos(sucursal.getEmpresa() != null ? sucursal.getEmpresa().getRazonsocial() : "Principal");
        u.setEmail(empresaUsername + "@minimarket.local");
        u.setDni("00000001");
        u.setTurno("MAÑANA");
        u.setEstado(1);
        u.setEnable(true);
        u.setFechaCreacion(LocalDateTime.now());
        u.setRol(rolEmpresa);
        u.setSucursal(sucursal);
        usuarioRepository.save(u);
        log.warn("Seed: usuario '{}' creado con rol EMPRESA. Cambia su contrasena antes de pasar a produccion.", empresaUsername);
    }

    /** Solo cuando la base no tiene ninguna sucursal: lo minimo para que el login y el punto de venta funcionen. */
    private Sucursales crearEmpresaYSucursalBase() {
        Empresas empresa = new Empresas();
        empresa.setRazonsocial("GADBACORP");
        empresa.setRuc("20000000001");
        empresa.setCiudad("Tarapoto");
        empresa.setDireccion("Por definir");
        empresa.setCorreo("contacto@minimarket.local");
        empresa.setFechaRegistro(LocalDate.now());
        empresa.setEstado(1);
        empresa.setPropietario("Propietario Demo");
        empresa.setTelefono("+51 900 000 000");
        empresa.setDistrito("Tarapoto");
        empresa.setPlan(planRepository.findByNombre("Empresarial").orElse(null));
        empresa.setCiclo("MENSUAL");
        empresa.setFechaVencimiento(LocalDate.now().plusYears(1));
        empresa = empresasRepository.save(empresa);

        Sucursales sucursal = new Sucursales();
        sucursal.setNombreSucursal("Sucursal Principal");
        sucursal.setContacto("000000000");
        sucursal.setDireccion("Por definir");
        sucursal.setEstado(1);
        sucursal.setEmpresa(empresa);
        sucursal = sucursalesRepository.save(sucursal);

        for (String nombre : List.of("Efectivo", "Yape", "Tarjeta")) {
            MetodosPago metodo = new MetodosPago();
            metodo.setNombre(nombre);
            metodo.setEstado(1);
            metodo.setSucursal(sucursal);
            metodosPagoRepository.save(metodo);
        }

        Caja caja = new Caja();
        caja.setNombreCaja("Caja 1");
        caja.setSaldoActual(0.0);
        caja.setEstado(1);
        caja.setEstadoCaja("LIBRE");
        caja.setSucursales(sucursal);
        cajaRepository.save(caja);

        log.info("Seed: empresa, sucursal, metodos de pago y caja base creados");
        return sucursal;
    }
}

package com.gadbacorp.api.service.jpa.inventario;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.tenant.TenantService;
import com.gadbacorp.api.entity.inventario.InventarioProductosDTO;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.inventario.ProductosDTO;
import com.gadbacorp.api.repository.inventario.CategoriasRepository;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.inventario.TipoProductoRepository;
import com.gadbacorp.api.repository.inventario.UnidadDeMedidaRepository;
import com.gadbacorp.api.service.inventario.IProductosService;

@Service
@Transactional
public class ProductosService implements IProductosService {

    @Autowired private ProductosRepository repoProd;
    @Autowired private CategoriasRepository repoCat;
    @Autowired private UnidadDeMedidaRepository repoUni;
    @Autowired private TipoProductoRepository repoTipo;
    @Autowired private InventarioProductoRepository repoInvProd;
    @Autowired private InventarioRepository repoInv;
    @Autowired private StockService stockService;
    @Autowired private com.gadbacorp.api.repository.inventario.ProductoPresentacionRepository repoPresentaciones;
    @Autowired private com.gadbacorp.api.repository.administrable.EmpresasRepository repoEmpresas;

    @Override
    public List<Productos> buscarTodos() {
        return repoProd.findAll().stream().filter(this::esVisible).collect(Collectors.toList());
    }

    @Override
    public Optional<Productos> buscarId(Integer id) {
        return repoProd.findById(id).filter(this::esVisible);
    }

    /** Cada empresa solo ve su propio catalogo (SUPERADMIN y procesos internos ven todo). */
    private boolean esVisible(Productos p) {
        return TenantService.puedeVer(TenantService.empresaDe(p.getEmpresa()));
    }

    @Override
    public Productos guardar(Productos producto) {
        return repoProd.save(producto);
    }

    @Override
    public Productos modificar(Productos producto) {
        return repoProd.save(producto);
    }

    @Override
    public void eliminar(Integer id) {
        buscarId(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado id=" + id));
        if (repoInvProd.existsByProducto_Idproducto(id)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No se puede eliminar: el producto está presente en inventario");
        }
        repoProd.deleteById(id);
    }

    @Override
    public List<Productos> listarProductosPorCategoria(Integer id) {
        return repoProd.findByCategoria_Idcategoria(id).stream().filter(this::esVisible).collect(Collectors.toList());
    }

    public List<ProductosDTO> listarProductosDTO() {
        return buscarTodos().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public ProductosDTO obtenerProductoDTO(Integer id) {
        Productos p = buscarId(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Producto no encontrado id=" + id));
        return toDTO(p);
    }

    public List<ProductosDTO> listarProductosPorCategoriaDTO(Integer categoriaId) {
        return listarProductosPorCategoria(categoriaId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public ProductosDTO crearProducto(ProductosDTO dto) {
        validarDTO(dto, false);
        Productos entidad = toEntity(dto);
        return toDTO(repoProd.save(entidad));
    }

    public ProductosDTO actualizarProducto(ProductosDTO dto) {
        if (dto.getIdproducto() == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "El campo 'idproducto' es obligatorio");
        }
        buscarId(dto.getIdproducto())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Producto no encontrado id=" + dto.getIdproducto()));
        validarDTO(dto, true);
        Productos entidad = toEntity(dto);
        return toDTO(repoProd.save(entidad));
    }

    /**
     * Busca por lo que lee el lector del punto de venta: codigo de barras del producto, SKU o codigo de barras de una
     * presentacion (entonces devuelve tambien la presentacion). Solo dentro del catalogo de la empresa del usuario.
     */
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> buscarPorCodigo(String codigo) {
        String c = codigo == null ? "" : codigo.trim();
        Integer empresa = TenantService.empresaActual();
        Optional<Productos> producto = empresa != null
            ? repoProd.findByCodigoBarrasAndEmpresa_Idempresa(c, empresa)
            : repoProd.findByCodigoBarrasAndEmpresaIsNull(c);
        String coincidencia = "CODIGO_BARRAS";
        com.gadbacorp.api.entity.inventario.ProductoPresentacion presentacion = null;
        if (producto.isEmpty()) {
            producto = empresa != null ? repoProd.findByCodigoIgnoreCaseAndEmpresa_Idempresa(c, empresa)
                : repoProd.findByCodigoIgnoreCaseAndEmpresaIsNull(c);
            coincidencia = "SKU";
        }
        if (producto.isEmpty()) {
            for (com.gadbacorp.api.entity.inventario.ProductoPresentacion p : repoPresentaciones.findByCodigoBarras(c)) {
                Optional<Productos> dueno = buscarId(p.getProducto().getIdproducto());
                if (dueno.isPresent()) {
                    producto = dueno;
                    presentacion = p;
                    coincidencia = "PRESENTACION";
                    break;
                }
            }
        }
        Productos encontrado = producto.orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "No hay un producto con el código " + c));
        java.util.Map<String, Object> r = new java.util.LinkedHashMap<>();
        r.put("coincidencia", coincidencia);
        r.put("producto", toDTO(encontrado));
        r.put("idPresentacion", presentacion == null ? null : presentacion.getIdPresentacion());
        r.put("presentacion", presentacion == null ? null : presentacion.getNombre());
        return r;
    }

    /** Productos cuyo stock en la sucursal es igual o menor a su stock mínimo, los más urgentes primero. */
    @Transactional(readOnly = true)
    public List<java.util.Map<String, Object>> stockBajo(Integer idSucursal) {
        return repoProd.findByStockMinimoNotNull().stream().filter(this::esVisible)
            .map(p -> {
                java.math.BigDecimal stock = stockService.stockTotal(idSucursal, p.getIdproducto());
                java.util.Map<String, Object> fila = new java.util.LinkedHashMap<>();
                fila.put("idproducto", p.getIdproducto());
                fila.put("nombre", p.getNombre());
                fila.put("codigo", p.getCodigo());
                fila.put("unidad", p.getUnidadMedida() != null ? p.getUnidadMedida().getCodigo() : null);
                fila.put("stockActual", stock);
                fila.put("stockMinimo", p.getStockMinimo());
                fila.put("faltante", p.getStockMinimo().subtract(stock));
                return fila;
            })
            .filter(f -> ((java.math.BigDecimal) f.get("faltante")).signum() >= 0)
            .sorted(java.util.Comparator.comparing(f -> ((java.math.BigDecimal) f.get("stockActual"))))
            .collect(Collectors.toList());
    }

    private void validarDTO(ProductosDTO dto, boolean isUpdate) {
        if (!isUpdate && dto.getIdproducto() != null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "No debe enviar idproducto al crear");
        }
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "El campo 'nombre' es obligatorio");
        }
        if (dto.getFechaVencimiento() != null
            && dto.getFechaVencimiento().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La fecha de vencimiento no puede ser anterior a hoy");
        }
        if (dto.getStockMinimo() != null && dto.getStockMinimo().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El stock mínimo no puede ser negativo");
        }
        if (dto.getCostoVenta() != null && dto.getCostoVenta().signum() < 0
            || dto.getCostoCompra() != null && dto.getCostoCompra().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los precios no pueden ser negativos");
        }
        validarCodigoUnico(dto);
        if (dto.getIdcategoria() == null || dto.getIdunidadmedida() == null || dto.getIdtipoproducto() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Categoría, unidad de medida y tipo de producto son obligatorios");
        }
        repoCat.findById(dto.getIdcategoria()).orElseThrow(() ->
            new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Categoría no encontrada id=" + dto.getIdcategoria()));
        repoUni.findById(dto.getIdunidadmedida()).orElseThrow(() ->
            new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Unidad de medida no encontrada id=" + dto.getIdunidadmedida()));
        repoTipo.findById(dto.getIdtipoproducto()).orElseThrow(() ->
            new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Tipo de producto no encontrado id=" + dto.getIdtipoproducto()));
    }

    private void validarCodigoUnico(ProductosDTO dto) {
        Integer empresa = TenantService.empresaActual();
        if (dto.getCodigo() != null && !dto.getCodigo().isBlank()) {
            (empresa != null ? repoProd.findByCodigoIgnoreCaseAndEmpresa_Idempresa(dto.getCodigo().trim(), empresa)
                    : repoProd.findByCodigoIgnoreCaseAndEmpresaIsNull(dto.getCodigo().trim())).ifPresent(otro -> {
                if (!otro.getIdproducto().equals(dto.getIdproducto())) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Ya existe un producto con el código " + dto.getCodigo());
                }
            });
        }
        if (dto.getCodigoBarras() != null && !dto.getCodigoBarras().isBlank()) {
            (empresa != null ? repoProd.findByCodigoBarrasAndEmpresa_Idempresa(dto.getCodigoBarras().trim(), empresa)
                    : repoProd.findByCodigoBarrasAndEmpresaIsNull(dto.getCodigoBarras().trim())).ifPresent(otro -> {
                if (!otro.getIdproducto().equals(dto.getIdproducto())) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Ya existe un producto con el código de barras " + dto.getCodigoBarras());
                }
            });
        }
    }

    private Productos toEntity(ProductosDTO dto) {
        Productos p = dto.getIdproducto() != null
            ? buscarId(dto.getIdproducto()).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Producto no encontrado id=" + dto.getIdproducto()))
            : new Productos();
        if (p.getIdproducto() == null && TenantService.empresaActual() != null) {
            p.setEmpresa(repoEmpresas.findById(TenantService.empresaActual()).orElse(null));
        }
        p.setNombre(dto.getNombre());
        p.setCodigo(blankToNull(dto.getCodigo()));
        p.setCodigoBarras(blankToNull(dto.getCodigoBarras()));
        p.setMarca(blankToNull(dto.getMarca()));
        p.setStockMinimo(dto.getStockMinimo());
        p.setAfectoIgv(dto.getAfectoIgv() != null ? dto.getAfectoIgv() : Boolean.TRUE);
        p.setDescripcion(dto.getDescripcion());
        p.setFechaVencimiento(dto.getFechaVencimiento());
        p.setTipoImpuesto(dto.getTipoImpuesto());
        p.setCostoCompra(dto.getCostoCompra());
        p.setCostoVenta(dto.getCostoVenta());
        p.setCostoMayor(dto.getCostoMayor());
        p.setImagen(dto.getImagen());
        p.setCategoria(repoCat.findById(dto.getIdcategoria()).get());
        p.setUnidadMedida(repoUni.findById(dto.getIdunidadmedida()).get());
        p.setTipoProducto(repoTipo.findById(dto.getIdtipoproducto()).get());

        // Si el cliente no envía inventario se conserva el stock actual (editar un producto no debe borrarlo)
        if (dto.getInventarioProducto() != null) {
            p.getInventarioProductos().clear();
            dto.getInventarioProducto().forEach(ipDto -> {
                InventarioProducto ip = new InventarioProducto();
                ip.setProducto(p);
                ip.setInventario(repoInv.findById(ipDto.getIdinventario())
                    .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Inventario no encontrado id=" + ipDto.getIdinventario())));
                ip.setStockactual(ipDto.getStockactual());
                ip.setFechaingreso(ipDto.getFechaingreso());
                p.getInventarioProductos().add(ip);
            });
        }
        return p;
    }

    private static String blankToNull(String v) {
        return v == null || v.isBlank() ? null : v.trim();
    }

    private ProductosDTO toDTO(Productos p) {
        ProductosDTO dto = new ProductosDTO();
        dto.setIdproducto(p.getIdproducto());
        dto.setIdempresa(TenantService.empresaDe(p.getEmpresa()));
        dto.setNombre(p.getNombre());
        dto.setCodigo(p.getCodigo());
        dto.setCodigoBarras(p.getCodigoBarras());
        dto.setMarca(p.getMarca());
        dto.setStockMinimo(p.getStockMinimo());
        dto.setAfectoIgv(p.getAfectoIgv());
        dto.setDescripcion(p.getDescripcion());
        dto.setFechaVencimiento(p.getFechaVencimiento());
        dto.setTipoImpuesto(p.getTipoImpuesto());
        dto.setCostoCompra(p.getCostoCompra());
        dto.setCostoVenta(p.getCostoVenta());
        dto.setCostoMayor(p.getCostoMayor());
        dto.setImagen(p.getImagen());
        dto.setIdcategoria(p.getCategoria() != null ? p.getCategoria().getIdcategoria() : null);
        dto.setIdunidadmedida(p.getUnidadMedida() != null ? p.getUnidadMedida().getIdunidadmedida() : null);
        dto.setIdtipoproducto(p.getTipoProducto() != null ? p.getTipoProducto().getIdtipoproducto() : null);

        dto.setInventarioProducto(
            p.getInventarioProductos()
             .stream()
             .map(ip -> new InventarioProductosDTO(
                 ip.getIdinventarioproducto(),
                 ip.getStockactual(),
                 ip.getFechaingreso(),
                 p.getIdproducto(),
                 ip.getInventario().getIdinventario()
             ))
             .collect(Collectors.toList())
        );
        return dto;
    }
    public void eliminarProducto(Integer id) {
    eliminar(id);
}
}

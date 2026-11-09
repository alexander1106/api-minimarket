package com.gadbacorp.api.host;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag;

/** Intercambio batch con el Host (mainframe): exportar ventas y validar un archivo recibido. */
@Tag(name = "Host - Intercambio con mainframe")
@RestController
@RequestMapping("/api/minimarket/host")
public class HostController {

    private final HostVentasService hostVentasService;

    public HostController(HostVentasService hostVentasService) {
        this.hostVentasService = hostVentasService;
    }

    /** Archivo VENTAS.AAAAMMDD.EBC: registros de 60 bytes en EBCDIC con importes COMP-3. */
    @GetMapping(value = "/ventas/exportar", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> exportar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Integer idSucursal) {
        byte[] archivo = hostVentasService.exportar(fecha, idSucursal);
        String nombre = "VENTAS." + fecha.format(DateTimeFormatter.BASIC_ISO_DATE) + ".EBC";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(archivo);
    }

    /** Valida un archivo recibido del Host (estructura y totales de control) y devuelve su contenido en JSON. */
    @PostMapping(value = "/ventas/validar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> validar(@RequestParam("archivo") MultipartFile archivo) throws java.io.IOException {
        try {
            HostVentasService.Lectura lectura = hostVentasService.leer(archivo.getBytes());
            Map<String, Object> respuesta = new LinkedHashMap<>();
            respuesta.put("valido", true);
            respuesta.put("fechaGeneracion", lectura.cabecera().fechaGeneracion());
            respuesta.put("origen", lectura.cabecera().origen());
            respuesta.put("cantidad", lectura.totales().cantidad());
            respuesta.put("total", lectura.totales().total());
            respuesta.put("ventas", lectura.ventas());
            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("valido", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

package com.gadbacorp.api.service.jpa.ventas;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.entity.ventas.DetallesVentas;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/** Comprobante de venta (boleta/factura) en PDF. */
@Service
public class VentaPdfService {

    private static final Font TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
    private static final Font NEGRITA = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 10);

    public byte[] generar(Ventas venta, List<DetallesVentas> detalles) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            Clientes cliente = venta.getCliente();
            Sucursales sucursal = cliente != null ? cliente.getSucursal() : null;
            Empresas empresa = sucursal != null ? sucursal.getEmpresa() : null;

            PdfPTable cabecera = new PdfPTable(new float[] {3f, 2f});
            cabecera.setWidthPercentage(100);
            PdfPCell datosEmpresa = new PdfPCell();
            datosEmpresa.setBorder(0);
            datosEmpresa.addElement(new Paragraph(texto(empresa != null ? empresa.getRazonsocial() : null, "MINIMARKET"), TITULO));
            if (empresa != null) {
                datosEmpresa.addElement(new Paragraph(texto(empresa.getDireccion(), "") + " " + texto(empresa.getCiudad(), ""), NORMAL));
            }
            cabecera.addCell(datosEmpresa);

            PdfPCell comprobante = new PdfPCell();
            comprobante.setHorizontalAlignment(Element.ALIGN_CENTER);
            comprobante.setPadding(8);
            comprobante.addElement(centrado("RUC: " + texto(empresa != null ? empresa.getRuc() : null, "-"), NEGRITA));
            comprobante.addElement(centrado(titulo(venta.getTipo_comprobante()), NEGRITA));
            comprobante.addElement(centrado(texto(venta.getNro_comrprobante(), "-"), NEGRITA));
            cabecera.addCell(comprobante);
            doc.add(cabecera);

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Fecha: " + texto(String.valueOf(venta.getFecha_venta()), "-"), NORMAL));
            if (cliente != null) {
                doc.add(new Paragraph("Cliente: " + texto(cliente.getNombre(), "") + " " + texto(cliente.getApellidos(), ""), NORMAL));
                doc.add(new Paragraph(texto(cliente.getTipoDocumento(), "Doc.") + ": " + texto(cliente.getDocumento(), "-"), NORMAL));
                doc.add(new Paragraph("Dirección: " + texto(cliente.getDireccion(), "-"), NORMAL));
            }
            doc.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(new float[] {1f, 5f, 2f, 2f});
            tabla.setWidthPercentage(100);
            for (String h : new String[] {"Cant.", "Descripción", "P. Unit.", "Subtotal"}) {
                PdfPCell c = new PdfPCell(new Phrase(h, NEGRITA));
                c.setBackgroundColor(new Color(230, 230, 230));
                tabla.addCell(c);
            }
            double suma = 0;
            for (DetallesVentas d : detalles) {
                String nombre = d.getProductos() != null ? d.getProductos().getNombre() : "";
                double sub = d.getSubTotal() != null ? d.getSubTotal() : 0;
                suma += sub;
                String cantidad = d.getCantidad() == null ? "0" : d.getCantidad().stripTrailingZeros().toPlainString();
                String descripcion = texto(nombre, "")
                        + (d.getPresentacion() != null ? " (" + d.getPresentacion().getNombre() + ")" : "");
                tabla.addCell(new Phrase(cantidad, NORMAL));
                tabla.addCell(new Phrase(descripcion, NORMAL));
                tabla.addCell(derecha(d.getPrecioUnitario() != null ? d.getPrecioUnitario() : 0));
                tabla.addCell(derecha(sub));
            }
            doc.add(tabla);

            double total = venta.getTotal_venta() != null ? venta.getTotal_venta() : suma;
            double igv = venta.getIgv() != null ? venta.getIgv().doubleValue() : 0;
            double gravada = venta.getOpGravada() != null ? venta.getOpGravada().doubleValue() : 0;
            double inafecta = venta.getOpInafecta() != null ? venta.getOpInafecta().doubleValue() : 0;
            Paragraph pTotal = new Paragraph(String.format(Locale.US,
                    "Op. gravada: %.2f   Op. inafecta: %.2f   IGV: %.2f      TOTAL: S/ %.2f", gravada, inafecta, igv, total), NEGRITA);
            pTotal.setAlignment(Element.ALIGN_RIGHT);
            pTotal.setSpacingBefore(10);
            doc.add(pTotal);
        } catch (DocumentException e) {
            throw new IllegalStateException("No se pudo generar el PDF de la venta", e);
        } finally {
            if (doc.isOpen()) {
                doc.close();
            }
        }
        return out.toByteArray();
    }

    private static Paragraph centrado(String t, Font f) {
        Paragraph p = new Paragraph(t, f);
        p.setAlignment(Element.ALIGN_CENTER);
        return p;
    }

    private static PdfPCell derecha(double valor) {
        PdfPCell c = new PdfPCell(new Phrase(String.format(Locale.US, "%.2f", valor), NORMAL));
        c.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return c;
    }

    private static String titulo(String tipo) {
        return "FACTURA".equalsIgnoreCase(tipo) ? "FACTURA ELECTRÓNICA" : "BOLETA DE VENTA";
    }

    private static String texto(String valor, String porDefecto) {
        return valor == null || valor.isBlank() || "null".equals(valor) ? porDefecto : valor;
    }
}

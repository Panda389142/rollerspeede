package com.rollerspeed.rollerspeed.service;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.rollerspeed.rollerspeed.model.Clase;
import com.rollerspeed.rollerspeed.model.Pago;
import com.rollerspeed.rollerspeed.model.Usuario;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

    public byte[] generarPdfUsuarios(List<Usuario> usuarios) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph("Reporte de Usuarios"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.addCell("ID");
            table.addCell("Nombre");
            table.addCell("Email");
            table.addCell("Rol");

            for (Usuario usuario : usuarios) {
                table.addCell(String.valueOf(usuario.getId()));
                table.addCell(usuario.getNombre());
                table.addCell(usuario.getEmail());
                table.addCell(usuario.getRol().name());
            }

            document.add(table);
        }
        return baos.toByteArray();
    }

    public byte[] generarPdfPagos(List<Pago> pagos) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph("Reporte de Pagos"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            table.addCell("ID");
            table.addCell("Monto");
            table.addCell("Fecha de Pago");
            table.addCell("Estado");
            table.addCell("Usuario");

            for (Pago pago : pagos) {
                table.addCell(String.valueOf(pago.getId()));
                table.addCell(String.valueOf(pago.getMonto()));
                table.addCell(pago.getFechaPago().toString());
                table.addCell(pago.getEstado().name());
                table.addCell(pago.getUsuario().getNombre());
            }

            document.add(table);
        }
        return baos.toByteArray();
    }

    public byte[] generarPdfClases(List<Clase> clases) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph("Reporte de Clases"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.addCell("ID");
            table.addCell("Nombre");
            table.addCell("Instructor");
            table.addCell("Horario");

            for (Clase clase : clases) {
                table.addCell(String.valueOf(clase.getId()));
                table.addCell(clase.getNombre());
                table.addCell(clase.getInstructor().getNombre());
                table.addCell(clase.getDiaSemana() + " " + clase.getHoraInicio() + "-" + clase.getHoraFin());
            }

            document.add(table);
        }
        return baos.toByteArray();
    }
}
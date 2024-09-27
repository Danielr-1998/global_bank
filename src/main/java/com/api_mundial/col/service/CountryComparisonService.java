package com.api_mundial.col.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class CountryComparisonService {

    private final ObjectMapper objectMapper;

    public CountryComparisonService() {
        this.objectMapper = new ObjectMapper();
    }

    public byte[] generarPdfComparacion(String pais1, String pais2, String pais1Data, String pais2Data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Agregar título
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Comparación entre " + pais1 + " y " + pais2, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(Chunk.NEWLINE); // Espacio

            // Agregar tabla para la comparación
            PdfPTable table = new PdfPTable(3); // 3 columnas: atributo, país 1, país 2
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Agregar encabezados de tabla
            PdfPCell headerAtributo = new PdfPCell(new Phrase("Atributo"));
            headerAtributo.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerAtributo.setBackgroundColor(BaseColor.LIGHT_GRAY);
            PdfPCell header1 = new PdfPCell(new Phrase(pais1));
            header1.setHorizontalAlignment(Element.ALIGN_CENTER);
            header1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            PdfPCell header2 = new PdfPCell(new Phrase(pais2));
            header2.setHorizontalAlignment(Element.ALIGN_CENTER);
            header2.setBackgroundColor(BaseColor.LIGHT_GRAY);

            table.addCell(headerAtributo);
            table.addCell(header1);
            table.addCell(header2);

            // Parsear datos del país 1 y país 2
            Map<String, Object> datosPais1 = parsearDatosPais(pais1Data);
            Map<String, Object> datosPais2 = parsearDatosPais(pais2Data);

            // Llenar la tabla con los datos más relevantes
            agregarFilaATabla(table, "Nombre", datosPais1.get("name"), datosPais2.get("name"));
            agregarFilaATabla(table, "Capital", datosPais1.get("capital"), datosPais2.get("capital"));
            agregarFilaATabla(table, "Población", datosPais1.get("population"), datosPais2.get("population"));
            agregarFilaATabla(table, "Área", datosPais1.get("area"), datosPais2.get("area"));
            agregarFilaATabla(table, "Moneda", datosPais1.get("currencies"), datosPais2.get("currencies"));
            agregarFilaATabla(table, "Bandera", datosPais1.get("flag"), datosPais2.get("flag"));

            // Agregar la tabla al documento PDF
            document.add(table);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    // Método para parsear los datos de un país usando Jackson
    private Map<String, Object> parsearDatosPais(String paisData) {
        Map<String, Object> datosPais = new HashMap<>();
        try {
            // Convertir el JSON de entrada en un árbol de nodos JsonNode
            JsonNode root = objectMapper.readTree(paisData);

            // Asumimos que el JSON es un array y obtenemos el primer elemento
            JsonNode pais = root.get(0);

            // Extraer los campos importantes del país
            datosPais.put("name", pais.path("name").path("official").asText("N/A"));
            datosPais.put("capital", pais.path("capital").isArray() ? pais.path("capital").get(0).asText("N/A") : "N/A");
            datosPais.put("population", pais.path("population").asLong(0));
            datosPais.put("area", pais.path("area").asDouble(0.0));
            datosPais.put("currencies", obtenerMoneda(pais.path("currencies")));
            datosPais.put("flag", pais.path("flags").path("png").asText("N/A"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return datosPais;
    }

    // Método auxiliar para obtener el nombre y símbolo de la moneda
    private String obtenerMoneda(JsonNode currenciesNode) {
        if (currenciesNode.isObject()) {
            for (JsonNode currency : currenciesNode) {
                String nombreMoneda = currency.path("name").asText("N/A");
                String simboloMoneda = currency.path("symbol").asText("N/A");
                return nombreMoneda + " (" + simboloMoneda + ")";
            }
        }
        return "N/A";
    }

    // Método para agregar una fila a la tabla de comparación
    private void agregarFilaATabla(PdfPTable table, String atributo, Object valor1, Object valor2) {
        PdfPCell cellAtributo = new PdfPCell(new Phrase(atributo));
        PdfPCell cell1 = new PdfPCell(new Phrase(valor1 != null ? valor1.toString() : "N/A"));
        PdfPCell cell2 = new PdfPCell(new Phrase(valor2 != null ? valor2.toString() : "N/A"));

        table.addCell(cellAtributo); // Agregar el nombre del atributo
        table.addCell(cell1);        // Agregar el valor para el país 1
        table.addCell(cell2);        // Agregar el valor para el país 2
    }
}

package com.miprimerapagina.springboot.app.view.pdf;

import java.awt.Color;
import java.io.IOException;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.miprimerapagina.springboot.app.models.entity.Factura;
import com.miprimerapagina.springboot.app.models.entity.ItemFactura;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("factura/ver")
public class FacturaPdfView extends AbstractPdfView {

	/*
	 * @Override protected void buildPdfDocument(Map<String, Object> model, Document
	 * document, PdfWriter writer, HttpServletRequest request, HttpServletResponse
	 * response) throws Exception {
	 * 
	 * Factura factura = (Factura) model.get("factura");
	 * 
	 * // Creamos la primera tabla que contendrá los datos del cliente PdfPTable
	 * tabla = new PdfPTable(1); tabla.setSpacingAfter(20);
	 * 
	 * PdfPCell cell = null;
	 * 
	 * cell = new PdfPCell(new Phrase("Datos del Cliente"));
	 * cell.setBackgroundColor(new Color(156, 137, 184)); cell.setPadding(8f);
	 * tabla.addCell(cell);
	 * 
	 * 
	 * tabla.addCell(factura.getCliente().getNombre() + " " +
	 * factura.getCliente().getApellido());
	 * tabla.addCell(factura.getCliente().getEmail());
	 * 
	 * // Creamos la segunda tabla que contendrá los datos de la factura PdfPTable
	 * tabla2 = new PdfPTable(1); tabla2.setSpacingAfter(20);
	 * 
	 * cell = new PdfPCell(new Phrase("Datos de la Factura"));
	 * cell.setBackgroundColor(new Color(156, 137, 184)); cell.setPadding(8f);
	 * 
	 * tabla2.addCell(cell); tabla2.addCell("Folio: " + factura.getId());
	 * tabla2.addCell("Descripción: " + factura.getDescripcion());
	 * tabla2.addCell("Fecha: " + factura.getCreateAt());
	 * 
	 * // Agregamos las dos tablas al documento document.add(tabla);
	 * document.add(tabla2);
	 * 
	 * PdfPTable tabla3 = new PdfPTable(4); tabla3.setWidths(new float[] {2.5f, 1,
	 * 1, 1});
	 * 
	 * String[] titulos = {"Producto", "Precio", "Cantidad", "Total"};
	 * 
	 * for (String titulo : titulos) { cell = new PdfPCell(new Phrase(titulo));
	 * cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	 * cell.setBackgroundColor(new Color(156, 137, 184)); tabla3.addCell(cell); }
	 * 
	 * for (ItemFactura item : factura.getItems()) {
	 * tabla3.addCell(item.getProducto().getNombre());
	 * 
	 * PdfPCell precioCell = new PdfPCell(new
	 * Phrase(item.getProducto().getPrecio().toString()));
	 * precioCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	 * tabla3.addCell(precioCell);
	 * 
	 * PdfPCell cantidadCell = new PdfPCell(new
	 * Phrase(item.getCantidad().toString()));
	 * cantidadCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	 * tabla3.addCell(cantidadCell);
	 * 
	 * PdfPCell importeCell = new PdfPCell(new
	 * Phrase(item.calcularImporte().toString()));
	 * importeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	 * tabla3.addCell(importeCell); }
	 * 
	 * cell = new PdfPCell(new Phrase("Total: ")); cell.setColspan(3);
	 * cell.setHorizontalAlignment(PdfCell.ALIGN_RIGHT); tabla3.addCell(cell);
	 * tabla3.addCell(factura.getTotal().toString());
	 * 
	 * document.add(tabla3);
	 * 
	 * }
	 */

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		// Obtiene los datos de la factura del modelo
		Factura factura = (Factura) model.get("factura");

		PdfPCell cell = null;
		
		// Configuración del documento PDF
		document.setPageSize(PageSize.A4);
		document.setMargins(40, 40, 40, 40);
		document.addTitle("Factura");

		// Agrega los datos del cliente
		Paragraph datosCliente = new Paragraph();
		datosCliente.add(new Phrase("Nombre: " + factura.getCliente().getNombre()));
		datosCliente.add(Chunk.NEWLINE);
		datosCliente.add(new Phrase("Apellido: " + factura.getCliente().getApellido()));
		datosCliente.add(Chunk.NEWLINE);
		datosCliente.add(new Phrase("Email: " + factura.getCliente().getEmail()));
		datosCliente.setAlignment(Element.ALIGN_RIGHT);
		datosCliente.setSpacingAfter(20f); // Espacio después de los datos del cliente

		document.add(datosCliente);

		// Creamos la segunda tabla que contendrá los datos de la factura
		PdfPTable tabla2 = new PdfPTable(1);
		tabla2.setSpacingAfter(20f);
		tabla2.setHorizontalAlignment(Element.ALIGN_LEFT);

		cell = new PdfPCell(new Phrase("Datos de la Factura"));
		cell.setBackgroundColor(new Color(156, 137, 184));
		cell.setPadding(8f);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorderColor(new Color(156, 137, 184));
		cell.setBorderWidth(1f);

		tabla2.addCell(cell);
		tabla2.addCell("Folio: " + factura.getId());
		tabla2.addCell("Descripción: " + factura.getDescripcion());
		tabla2.addCell("Fecha: " + factura.getCreateAt());
		
		document.add(tabla2);

		// Agrega la tabla de productos
		PdfPTable tablaProductos = new PdfPTable(4);
		tablaProductos.setWidths(new float[] { 2.5f, 1, 1, 1 });
		tablaProductos.setWidthPercentage(100); // Ancho de la tabla: 100% del ancho disponible

		String[] titulos = { "Producto", "Precio", "Cantidad", "Total" };
		Font fontTitulos = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);

		for (String titulo : titulos) {
			cell = new PdfPCell(new Phrase(titulo, fontTitulos));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBackgroundColor(new Color(156, 137, 184));
			tablaProductos.addCell(cell);
		}

		for (ItemFactura item : factura.getItems()) {
			tablaProductos.addCell(item.getProducto().getNombre());

			PdfPCell precioCell = new PdfPCell(new Phrase(item.getProducto().getPrecio().toString()));
			precioCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			tablaProductos.addCell(precioCell);

			PdfPCell cantidadCell = new PdfPCell(new Phrase(item.getCantidad().toString()));
			cantidadCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			tablaProductos.addCell(cantidadCell);

			PdfPCell importeCell = new PdfPCell(new Phrase(item.calcularImporte().toString()));
			importeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			tablaProductos.addCell(importeCell);
		}

		PdfPCell totalCell = new PdfPCell(new Phrase("Total: "));
		totalCell.setColspan(3);
		totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		totalCell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical al medio
		totalCell.setBackgroundColor(new Color(156, 137, 184)); // Color de fondo
		totalCell.setBorderColor(new Color(156, 137, 184)); // Color del borde
		totalCell.setBorderWidth(1f); // Ancho del borde
		tablaProductos.addCell(totalCell);

		PdfPCell totalValueCell = new PdfPCell(new Phrase(factura.getTotal().toString()));
		totalValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		totalValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical al medio
		totalValueCell.setBackgroundColor(Color.LIGHT_GRAY); // Color de fondo
		totalValueCell.setBorderColor(new Color(156, 137, 184)); // Color del borde
		totalValueCell.setBorderWidth(1f); // Ancho del borde
		tablaProductos.addCell(totalValueCell);

		document.add(tablaProductos);
	}
}

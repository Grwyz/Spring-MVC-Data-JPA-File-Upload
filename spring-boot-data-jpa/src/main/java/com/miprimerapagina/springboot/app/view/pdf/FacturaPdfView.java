package com.miprimerapagina.springboot.app.view.pdf;

import java.awt.Color;
import java.util.Map;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.miprimerapagina.springboot.app.models.entity.Factura;
import com.miprimerapagina.springboot.app.models.entity.ItemFactura;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("factura/ver")
public class FacturaPdfView extends AbstractPdfView {

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		// Obtiene los datos de la factura del modelo
		Factura factura = (Factura) model.get("factura");

		PdfPCell cell = null;
		
		MessageSourceAccessor mensajes = getMessageSourceAccessor();

		/*
		// Configuración del documento PDF
		document.setPageSize(PageSize.A4);
		document.setMargins(40, 40, 40, 40);
		document.addTitle("Factura");
		*/
		
		float marginHeight = 40;
		// Se obtiene el tamaño de la página
        float pageWidth = document.getPageSize().getWidth();
        float pageHeight = document.getPageSize().getHeight();
        
        // Se obtiene un objeto para dibujar en el documento
        PdfContentByte contentByte = writer.getDirectContent();
        // Se establece el color con el que se rellenarán los rectángulos de los bordes superior e inferior
        contentByte.setColorFill(new Color(156, 137, 184));
        // Rectángulo borde superior
        contentByte.rectangle(0, pageHeight - marginHeight, pageWidth, marginHeight);
        // Rectángulo borde inferior
        contentByte.rectangle(0, 0, pageWidth, marginHeight);
        // Se rellenan los rectángulos
        contentByte.fill();
        
        // Establecer una fuente de color negro
        Font font = new Font();
        font.setColor(Color.BLACK);
		
		// Agregar el título "Factura"
		Paragraph tituloFactura = new Paragraph("Factura", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, Color.BLACK));
		tituloFactura.setAlignment(Element.ALIGN_CENTER);
		tituloFactura.setSpacingAfter(20f);
		document.add(tituloFactura);
		
		// Agregar barra
		PdfPTable barra = new PdfPTable(1);
		barra.setWidthPercentage(200);
		barra.getDefaultCell().setBackgroundColor(new Color(156, 137, 184));
		barra.getDefaultCell().setFixedHeight(3);
		barra.addCell("");
		document.add(barra);
		
		// Crear una tabla con una fila y tres celdas (Se agregarán los datos del cliente en la primera celda, una celda vacía
		// en la segunda y una tabla con los datos de la factura en la tercera
		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(100);
		table.setSpacingAfter(20f);
		table.setSpacingBefore(20f);
		
		// Párrafo para los datos del cliente con la fuente creada
		Paragraph datosCliente = new Paragraph();
		datosCliente.add(new Phrase(mensajes.getMessage("text.cliente.nombre") + ": " + factura.getCliente().getNombre(), font));
		datosCliente.add(Chunk.NEWLINE);
		datosCliente.add(new Phrase(mensajes.getMessage("text.cliente.apellido") + ": "  + factura.getCliente().getApellido(), font));
		datosCliente.add(Chunk.NEWLINE);
		datosCliente.add(new Phrase(mensajes.getMessage("text.cliente.email") + ": "  + factura.getCliente().getEmail(), font));
		datosCliente.setAlignment(Element.ALIGN_LEFT);
		datosCliente.setSpacingAfter(20f);
		
		// Tabla con los datos de la factura con la fuente creada
		PdfPTable tabla2 = new PdfPTable(1);
		tabla2.setSpacingAfter(20f);
		tabla2.setHorizontalAlignment(Element.ALIGN_LEFT);

		cell = new PdfPCell(new Phrase(mensajes.getMessage("text.factura.ver.datos.factura"), font));
		cell.setBackgroundColor(new Color(156, 137, 184));
		cell.setPadding(8f);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorderColor(new Color(156, 137, 184));
		cell.setBorderWidth(1f);

		tabla2.addCell(cell);
		PdfPCell cellText = new PdfPCell(new Phrase(mensajes.getMessage("text.cliente.factura.folio") + ": " + factura.getId(), font));
        tabla2.addCell(cellText);
        cellText.setPhrase(new Phrase(mensajes.getMessage("text.cliente.factura.descripcion") + ": " + factura.getDescripcion(), font));
        tabla2.addCell(cellText);
        cellText.setPhrase(new Phrase(mensajes.getMessage("text.cliente.factura.fecha") + ": " + factura.getCreateAt(), font));
        tabla2.addCell(cellText);

		// Primera celda: datos del cliente
		PdfPCell datosClienteCell = new PdfPCell();
		datosClienteCell.addElement(datosCliente);
		datosClienteCell.setBorder(Rectangle.NO_BORDER);
		datosClienteCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		datosClienteCell.setVerticalAlignment(Element.ALIGN_TOP); // Alineación vertical en la parte superior
		datosClienteCell.setBorderWidthRight(0); // Sin borde derecho
		
		//Agregar la primera celda a la tabla
		table.addCell(datosClienteCell);
		
		// Segunda celda: Celda vacía (Sirve para separar las otras dos celdas)
		PdfPCell emptyCell = new PdfPCell();
		emptyCell.setBorder(Rectangle.NO_BORDER);
		
		// Agregar la segunda celda a la tabla
		table.addCell(emptyCell);

		// Tercera celda: tabla2 (datos de la factura)
		PdfPCell tabla2Cell = new PdfPCell(tabla2);
		tabla2Cell.setBorder(Rectangle.NO_BORDER);
		tabla2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		tabla2Cell.setVerticalAlignment(Element.ALIGN_TOP); // Alineación vertical en la parte superior
		tabla2Cell.setBorderWidthLeft(0); // Sin borde izquierdo
		
		// Agregar la tercera celda a la tabla
		table.addCell(tabla2Cell);

		// Establecer anchos de las celdas para ocupar todo el ancho de la página
		float[] columnWidths = {0.5f, 1, 0.5f};
		table.setWidths(columnWidths);

		// Agregar la tabla al documento
		document.add(table);
		
		// Agregar barra
		document.add(barra);

		// Agrega la tabla de productos
		PdfPTable tablaProductos = new PdfPTable(4);
		tablaProductos.setSpacingBefore(20f);
		tablaProductos.setSpacingAfter(20f);
		tablaProductos.setWidths(new float[] { 2.5f, 1, 1, 1 });
		tablaProductos.setWidthPercentage(100);
		// Ancho de la tabla: 100% del ancho disponible

		String[] titulos = { mensajes.getMessage("text.factura.form.item.nombre"),
				mensajes.getMessage("text.factura.form.item.precio"),
				mensajes.getMessage("text.factura.form.item.cantidad"),
				mensajes.getMessage("text.factura.form.item.total") };
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

		PdfPCell totalCell = new PdfPCell(new Phrase(mensajes.getMessage("text.factura.form.total") + ": "));
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
	
	@Override
    protected Document newDocument() {
        // Establecer el tamaño de página
        return new Document(PageSize.A4);
    }

}

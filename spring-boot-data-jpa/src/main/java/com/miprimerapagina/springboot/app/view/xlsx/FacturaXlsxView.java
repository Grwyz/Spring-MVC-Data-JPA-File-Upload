package com.miprimerapagina.springboot.app.view.xlsx;

import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.miprimerapagina.springboot.app.models.entity.Factura;
import com.miprimerapagina.springboot.app.models.entity.ItemFactura;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("factura/ver.xlsx")
public class FacturaXlsxView extends AbstractXlsxView{

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		MessageSourceAccessor mensajes = getMessageSourceAccessor();
			
		// Cambiar el nombre del documento
		response.setHeader("Content-Disposition", "attatchment; filename=\"factura_view.xlsx\"");
		
		// Obtener la factura
		Factura factura = (Factura) model.get("factura");
		
		// Crear una hoja con nombre personalizado
		Sheet sheet = workbook.createSheet("Factura Spring");
		
		// Contador de filas
		int rownum = 0;
		
		// Forma de crear filas, celdas y establecer el valor de la celda
		Row row = sheet.createRow(rownum);
		Cell cell = row.createCell(0);
		cell.setCellValue(mensajes.getMessage("text.factura.ver.datos.cliente"));
		
		// Forma simplificada: Crear filas, celdas y establecer el valor de la celda
		// Se crean las celdas con los datos del cliente
		sheet.createRow(++rownum).createCell(0).setCellValue(mensajes.getMessage("text.cliente.nombre") + ": ");
		sheet.getRow(rownum).createCell(1).setCellValue(factura.getCliente().getNombre());
		sheet.createRow(++rownum).createCell(0).setCellValue(mensajes.getMessage("text.cliente.apellido") + ": ");
		sheet.getRow(rownum).createCell(1).setCellValue(factura.getCliente().getApellido());
		sheet.createRow(++rownum).createCell(0).setCellValue(mensajes.getMessage("text.cliente.email") + ": ");
		sheet.getRow(rownum++).createCell(1).setCellValue(factura.getCliente().getEmail());
		
		// Crear filas y celdas con los datos de la factura
		sheet.createRow(++rownum).createCell(0).setCellValue(mensajes.getMessage("text.factura.ver.datos.factura"));
		sheet.createRow(++rownum).createCell(0).setCellValue(mensajes.getMessage("text.cliente.factura.folio") + ": ");
		sheet.getRow(rownum).createCell(1).setCellValue(factura.getId());
		sheet.createRow(++rownum).createCell(0).setCellValue(mensajes.getMessage("text.cliente.factura.descripcion") + ": ");
		sheet.getRow(rownum).createCell(1).setCellValue(factura.getDescripcion());
		sheet.createRow(++rownum).createCell(0).setCellValue(mensajes.getMessage("text.cliente.factura.fecha") + ": ");
		
		// Crear estilo de celda para el formato de fecha
        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper creationHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd"));
        
        // Crear celda para la fecha
        sheet.getRow(rownum).createCell(1).setCellValue(factura.getCreateAt());
        sheet.getRow(rownum++).getCell(1).setCellStyle(dateCellStyle);
		
		// Estilos para los cabeceros de la tabla de productos (Header)
		CellStyle theaderStyle = workbook.createCellStyle();
		theaderStyle.setBorderBottom(BorderStyle.MEDIUM);
		theaderStyle.setBorderTop(BorderStyle.MEDIUM);
		theaderStyle.setBorderLeft(BorderStyle.MEDIUM);
		theaderStyle.setBorderRight(BorderStyle.MEDIUM);
		theaderStyle.setFillForegroundColor(IndexedColors.GOLD.index);
		theaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		theaderStyle.setAlignment(HorizontalAlignment.CENTER);
		
		// Estilos para los productos de la tabla de productos (Body)
		CellStyle tbodyStyle = workbook.createCellStyle();
		tbodyStyle.setBorderBottom(BorderStyle.THIN);
		tbodyStyle.setBorderTop(BorderStyle.THIN);
		tbodyStyle.setBorderLeft(BorderStyle.THIN);
		tbodyStyle.setBorderRight(BorderStyle.THIN);
		
		// Estilos para las cantidades de los productos de la tabla de productos (Body)
		CellStyle tbodyCantidadStyle = workbook.createCellStyle();
		tbodyCantidadStyle.setBorderBottom(BorderStyle.THIN);
		tbodyCantidadStyle.setBorderTop(BorderStyle.THIN);
		tbodyCantidadStyle.setBorderLeft(BorderStyle.THIN);
		tbodyCantidadStyle.setBorderRight(BorderStyle.THIN);
		tbodyCantidadStyle.setAlignment(HorizontalAlignment.CENTER);
		
		// Arreglo con los cabeceros de la tabla de productos
		String[] titulos = { mensajes.getMessage("text.factura.form.item.nombre"),
				mensajes.getMessage("text.factura.form.item.precio"),
				mensajes.getMessage("text.factura.form.item.cantidad"),
				mensajes.getMessage("text.factura.form.item.total") };
		
		// Crear la fila para los titulos
		sheet.createRow(++rownum);
		
		// Iterar los cabeceros de la tabla de productos y aplicar su respectivo estilo
		for(int i = 0; i < titulos.length; i++) {
			String titulo = titulos[i];
			sheet.getRow(rownum).createCell(i).setCellValue(titulo);
			sheet.getRow(rownum).getCell(i).setCellStyle(theaderStyle);
		}
		
		// Iterar los productos de la factura y aplicar su respectivo estilo
		for(ItemFactura item: factura.getItems()) {
			int cellnum = 0;
			Row fila = sheet.createRow(++rownum);
			fila.createCell(cellnum).setCellValue(item.getProducto().getNombre());
			fila.getCell(cellnum++).setCellStyle(tbodyStyle);
			fila.createCell(cellnum).setCellValue(item.getProducto().getPrecio());
			fila.getCell(cellnum++).setCellStyle(tbodyCantidadStyle);
			fila.createCell(cellnum).setCellValue(item.getCantidad());
			fila.getCell(cellnum++).setCellStyle(tbodyCantidadStyle);
			fila.createCell(cellnum).setCellValue(item.calcularImporte());
			fila.getCell(cellnum++).setCellStyle(tbodyCantidadStyle);
		}
		
		// Ajustar la longitud de las celdas automáticamente
        for (int i = 0; i < sheet.getRow(rownum).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
		
		// Fila y celdas para el gran total
		Row filaTotal = sheet.createRow(++rownum);
		filaTotal.createCell(2).setCellValue(mensajes.getMessage("text.factura.form.total") + ": ");
		filaTotal.getCell(2).setCellStyle(tbodyStyle);
		filaTotal.createCell(3).setCellValue(factura.getTotal());
		filaTotal.getCell(3).setCellStyle(tbodyCantidadStyle);
		
		// Ajustamos la longitud de las celdas 2 y 3
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		
	}

}

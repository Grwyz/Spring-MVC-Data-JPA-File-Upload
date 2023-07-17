package com.miprimerapagina.springboot.app.view.xlsx;

import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
			
		Factura factura = (Factura) model.get("factura");
		Sheet sheet = workbook.createSheet("Factura Spring");
		
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("Datos del cliente");
		
		sheet.createRow(1).createCell(0).setCellValue("Nombre: ");
		sheet.getRow(1).createCell(1).setCellValue(factura.getCliente().getNombre());
		sheet.createRow(2).createCell(0).setCellValue("Apellido: ");
		sheet.getRow(2).createCell(1).setCellValue(factura.getCliente().getApellido());
		sheet.createRow(3).createCell(0).setCellValue("Email: ");
		sheet.getRow(3).createCell(1).setCellValue(factura.getCliente().getEmail());
		
		sheet.createRow(5).createCell(0).setCellValue("Datos de la factura");
		sheet.createRow(6).createCell(0).setCellValue("Folio: ");
		sheet.getRow(6).createCell(1).setCellValue(factura.getId());
		sheet.createRow(7).createCell(0).setCellValue("Descripción: ");
		sheet.getRow(7).createCell(1).setCellValue(factura.getDescripcion());
		sheet.createRow(8).createCell(0).setCellValue("Fecha: ");
		sheet.getRow(8).createCell(1).setCellValue(factura.getCreateAt());
		
		String[] titulos = {"Producto", "Precio", "Cantidad", "Total"};
		sheet.createRow(10);
		
		for(int i = 0; i < titulos.length; i++) {
			String titulo = titulos[i];
			sheet.getRow(10).createCell(i).setCellValue(titulo);
		}
		
		int rownum = 11;
		for(ItemFactura item: factura.getItems()) {
			Row fila = sheet.createRow(rownum++);
			fila.createCell(0).setCellValue(item.getProducto().getNombre());
			fila.createCell(1).setCellValue(item.getProducto().getPrecio());
			fila.createCell(2).setCellValue(item.getCantidad());
			fila.createCell(3).setCellValue(item.calcularImporte());
		}
		
		Row filaTotal = sheet.createRow(rownum);
		filaTotal.createCell(2).setCellValue("Gran Total: ");
		filaTotal.createCell(3).setCellValue(factura.getTotal());
		
		
		
		/*
		sheet.createRow(10).createCell(0).setCellValue("Producto");
		sheet.getRow(10).createCell(1).setCellValue("Precio");
		sheet.getRow(10).createCell(2).setCellValue("Cantidad");
		sheet.getRow(10).createCell(3).setCellValue("Total");
		*/
		
		
		
		
		
	}

}

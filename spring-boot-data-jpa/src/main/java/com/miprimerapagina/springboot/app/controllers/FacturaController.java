package com.miprimerapagina.springboot.app.controllers;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.miprimerapagina.springboot.app.models.entity.Cliente;
import com.miprimerapagina.springboot.app.models.entity.Factura;
import com.miprimerapagina.springboot.app.models.entity.ItemFactura;
import com.miprimerapagina.springboot.app.models.entity.Producto;
import com.miprimerapagina.springboot.app.models.service.IClienteService;

import jakarta.validation.Valid;

@Secured("ROLE_ADMIN")
@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")
public class FacturaController {
	
	//Inyectamos las dependencias necesarias
	@Autowired
	private IClienteService clienteService;
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	//Método para ver la factura
	@GetMapping("/ver/{id}")
	public String ver(@PathVariable(value="id") Long id,
			Model model,
			RedirectAttributes flash) {
		
		//Obtenemos la factura a través del id
		Factura factura = clienteService.fetchFacturaByIdWithClienteWithItemFacturaWithProducto(id); //clienteService.findFacturaById(id);
		
		//Validamos que la factura no esté vacía
		if(factura == null) {
			//Mensaje - Indicamos que la factura no existe
			flash.addFlashAttribute("error", "La factura no existe en la base de datos");
			//Redirigimos a la vista "/listar"
			return "redirect:/listar";
		}
		
		//Pasamos los datos a la vista
		model.addAttribute("factura", factura);
		model.addAttribute("titulo", "Factura: ".concat(factura.getDescripcion()));
		
		//Regresamos la vista "factura/ver"
		return "factura/ver";
	}
	
	//Método para crear la factura
	@GetMapping("/form/{clienteId}")
	public String crear(@PathVariable(value="clienteId") Long clienteId, 
			Map<String, Object> model, 
			RedirectAttributes flash) {
		
		//Buscamos al cliente
		Cliente cliente = clienteService.findOne(clienteId);
		
		//Preguntamos si el cliente es nulo
		if(cliente == null) {
			//Mensaje - Indicamos que el cliente no existe
			flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
			//Redirigimos a la vista "/listar"
			return "redirect:/listar";
		}
		
		//Creamos un nuevo objeto del tipo "Factura"
		Factura factura = new Factura();
		//Asignamos un cliente a la factura
		factura.setCliente(cliente);
		
		//Pasamos los elementos a la vista
		model.put("factura", factura);
		model.put("titulo", "Crear factura");
		
		//Regresamos la vista "factura/form"
		return "factura/form";
	}
	
	@GetMapping(value="/cargar-productos/{term}", produces= {"application/json"})
	public @ResponseBody List<Producto> cargarProductos(@PathVariable String term){
		return clienteService.findByName(term);
	}
	
	//Método para Guardar la factura
	@PostMapping("/form")
	public String guardar(@Valid Factura factura,
			BindingResult result,
			Model model,
			@RequestParam(name="item_id[]", required = false) Long [] itemId,
			@RequestParam(name="cantidad[]", required = false) Integer[] cantidad,
			RedirectAttributes flash,
			SessionStatus status) {
		
		//Si el BindingResult encuentra algún error, redirige al formulario
		if(result.hasErrors()) {
			//Pasamos los elementos a la vista
			model.addAttribute("titulo", "Crear Factura");
			//Regresamos la vista "factura/form"
			return "factura/form";
		}
		
		//Preguntamos si la factura no tiene lineas
		if(itemId == null || itemId.length == 0) {
			
			//Pasamos los elementos a la vista
			model.addAttribute("titulo", "Crear Factura");
			model.addAttribute("error", "Error: La factura NO puede no tener lineas!");
			
			//Regresamos la vista "factura/form"
			return "factura/form";
		}
		
		//Iteramos cada linea y las agregamos a la factura
		for(int i = 0; i < itemId.length; i++) {
			Producto producto = clienteService.findProductoById(itemId[i]);
			
			//Creamos un objeto del tipo ItemFactura
			ItemFactura linea = new ItemFactura();
			//Agregamos la cantidad a la linea
			linea.setCantidad(cantidad[i]);
			//Agregamos el producto a la linea
			linea.setProducto(producto);
			//Agregamos la linea a la factura
			factura.addItemFactura(linea);
			
			log.info("ID: " + itemId[i].toString() + ", Cantidad: " + cantidad[i].toString());
		}
		
		//Guardamos la factura
		clienteService.saveFactura(factura);
		
		//Establecemos el estatus de la transacción como "complete"
		status.setComplete();
		
		//Mensaje - Indicamos que la factura fue creada con éxito
		flash.addFlashAttribute("success", "Factura creada con éxito!");
		
		//Redirigimos a la vista "/ver/{id}"
		return "redirect:/ver/" + factura.getCliente().getId();
	}
	
	//Método para eliminar una factura
	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(value="id") Long id, RedirectAttributes flash) {
		
		//Buscamos la factura a través del id
		Factura factura = clienteService.findFacturaById(id);
		
		//Validamos que la factura no sea nula
		if(factura != null) {
			
			//Eliminamos la factura
			clienteService.deleteFactura(id);
			//Mensaje - Indicamos que la factura ha sido eliminada
			flash.addFlashAttribute("success", "Factura eliminada con éxito!");
			//Redirigimos a la vista "/ver/{id}"
			return "redirect:/ver/" + factura.getCliente().getId();
		}
		//Mensaje - Si la factura es nula, muestra un mensaje indicando que no existe.
		flash.addFlashAttribute("error", "La factura no existe en la base de datos, no se pudo eliminar");
		
		//Redirigimos a la vista "/listar"
		return "redirect:/listar";
		
	}

}

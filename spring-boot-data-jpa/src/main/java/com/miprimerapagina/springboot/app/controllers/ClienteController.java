package com.miprimerapagina.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.miprimerapagina.springboot.app.models.entity.Cliente;
import com.miprimerapagina.springboot.app.models.service.IClienteService;
import com.miprimerapagina.springboot.app.models.service.IUploadFileService;
import com.miprimerapagina.springboot.app.util.paginator.PageRender;

import jakarta.validation.Valid;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	@Autowired
	private IClienteService clienteService;

	@Autowired
	private IUploadFileService uploadFileService;
	
	//Este método recibe un nombre de archivo como parte de la URL
	//Lo carga usando un servicio externo
	//Y devuelve una respuesta HTTP (200) con el archivo adjunto y
	//El nombre de archivo correspondiente
	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {

		Resource recurso = null;

		try {
			recurso = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attatchment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}

	//Método para ver a un cliente
	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		//Encontramos al cliente con el método "findOne(id)"
		Cliente cliente = clienteService.findOne(id);
		
		//Validamos que el cliente no sea nulo
		if (cliente == null) {
			flash.addAttribute("error", "El cliente no existe en la base de datos");
			return "redirect:/listar";
		}
		
		//Mandamos los datos a la vista
		model.put("cliente", cliente);
		model.put("titulo", "Detalle cliente: " + cliente.getNombre());
		
		//Regresamos la vista
		return "ver";
	}
	
	//Método para listar los clientes (Usamos Pageable, Page<Cliente> y PageRender<Cliente>
	//para mostrarlos en varias páginas)
	@RequestMapping(value = "/listar", method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
		
		//Elemento para especificar el número de clientes por página
		Pageable pageRequest = PageRequest.of(page, 4);
		
		//Obtenemos los clientes
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		
		//Inicializamos nuestro paginador con la vista y los clientes
		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);

		//Pasamos los datos a la vista
		model.addAttribute("titulo", "Listado de clientes");
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);
		
		//Regresamos la vista
		return "listar";
	}
	
	//Método para mostrar el formulario de clientes
	@RequestMapping(value = "/form")
	public String crear(Map<String, Object> model) {
		//Inicializamos un nuevo cliente sin datos
		Cliente cliente = new Cliente();
		
		//Pasamos los datos a la vista
		model.put("cliente", cliente);
		model.put("titulo", "Formulario de Cliente");
		
		//Regresamos la vista
		return "form";
	}

	@RequestMapping(value = "/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		
		//Inicializamos un cliente sin datos
		Cliente cliente = null;
		
		//Validamos que el id de un cliente sea mayor a 0
		if (id > 0) {
			
			//Buscamos al cliente
			cliente = clienteService.findOne(id);
			
			//Validamos que el cliente no sea nulo y que exista en la BBDD
			if (cliente == null) {
				flash.addFlashAttribute("error", "El ID del cliente no existe en la BBDD");
				return "redirect:/listar";
			}
		} else {
			flash.addFlashAttribute("error", "El ID del cliente no puede ser cero!");
			return "redirect:/listar";
		}
		//Pasamos los datos a la vista
		model.put("cliente", cliente);
		model.put("titulo", "Editar Cliente");
		
		//Regresamos la vista
		return "form";
	}
	
	//Método para crear / editar / guardar un cliente
	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
		
		//Validamos si el formulario tiene algún error
		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			return "form";
		}
		
		//Validamos que la foto no esté vacía
		if (!foto.isEmpty()) {
			
			//Validamos que el id no sea nulo, sea mayor que 0, que la foto no sea nula y que tenga una longitud mayor a 0
			if (cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null
					&& cliente.getFoto().length() > 0) {
				//Borramos la foto que tenga actualmente el cliente
				uploadFileService.delete(cliente.getFoto());
			}
			
			//Inicializamos una variable para guardar el nombre del archivo de imágen
			String uniqueFilename = null;
			//Try-catch para poner en la variable "uniqueFilename" el nombre de la foto
			try {
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Mensaje emergente - indica que la foto se subió correctamente
			flash.addFlashAttribute("info", "El archivo '" + uniqueFilename + "' se ha subido correctamente");
			
			//Coloca la foto almacenada en la variable "uniqueFilename" al cliente actual
			cliente.setFoto(uniqueFilename);

		}
		
		//Mensaje - Indica que el cliente fue editado (si el id no es nulo) o creado (si el id es nulo) con éxito
		String mensajeFlash = (cliente.getId() != null) ? "Cliente editado con éxito!" : "Cliente creado con éxito!";
		
		//Guardamos al cliente
		clienteService.save(cliente);
		
		//Completamos el estatus de la sesión
		status.setComplete();
		
		//Mensaje emergente - Mostramos el mensaje almacenado en la variable "mensajeFlash"
		flash.addFlashAttribute("success", mensajeFlash);
		
		//Redirigimos a la vista "listar"
		return "redirect:listar";
	}
	
	//Método para eliminar un cliente
	@RequestMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		//Validamos si el id del cliente es mayor a 0
		if (id > 0) {
			//Buscamos al cliente
			Cliente cliente = clienteService.findOne(id);
			
			//Eliminamos al cliente
			clienteService.delete(id);
			
			//Mensaje - Indicamos que el cliente fue eliminado
			flash.addFlashAttribute("success", "Cliente eliminado con éxito!");
			
			//Validamos si el se borró la foto del cliente (en caso de que tuviera)
			if (uploadFileService.delete(cliente.getFoto())) {
				//Mensaje - Indicamos que la foto del cliente fue eliminada
				flash.addFlashAttribute("info", "Foto: " + cliente.getFoto() + " eliminada con éxito!");
			}
		}
		
		//Redirigimos a la vista "listar"
		return "redirect:/listar";
	}
	
	//Método para mostrar la vista "bamboozle"
	@RequestMapping(value = "/bamboozle")
	public String home() {
		//Regresamos la vista "bamboozle"
		return "bamboozle";
	}

}

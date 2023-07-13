package com.miprimerapagina.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@SessionAttributes("cliente")
public class ClienteController {
	
	protected final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private IClienteService clienteService;

	@Autowired
	private IUploadFileService uploadFileService;
	
	@Autowired
	private MessageSource messageSource;
	
	/*
	 * Este método recibe un nombre de archivo como parte de la URL
	*Lo carga usando un servicio externo
	*Y devuelve una respuesta HTTP (200) con el archivo adjunto y
	*El nombre de archivo correspondiente
	*/
	@Secured("ROLE_USER")
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
	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash, Locale locale) {

		//Encontramos al cliente con el método "findOne(id)"
		Cliente cliente = clienteService.fetchByIdWithFacturas(id); //clienteService.findOne(id);
		
		//Validamos que el cliente no sea nulo
		if (cliente == null) {
			flash.addFlashAttribute("error", messageSource.getMessage("text.cliente.flash.db.error", null, locale));
			return "redirect:/listar";
		}
		
		//Mandamos los datos a la vista
		model.put("cliente", cliente);
		model.put("titulo", messageSource.getMessage("text.cliente.detalle.titulo", null, locale).concat(": ").concat(cliente.getNombre()));
		
		//Regresamos la vista
		return "ver";
	}
	
	//Método para listar los clientes (Usamos Pageable, Page<Cliente> y PageRender<Cliente>
	//para mostrarlos en varias páginas)
	@RequestMapping(value = {"/listar", "/", ""}, method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model,
			Authentication authentication,
			HttpServletRequest request,
			Locale locale) {
		
		//Validamos que el objeto "authentication" no sea nulo
		if(authentication != null) {
			logger.info("Hola, usuario autenticado. Tu username es: ".concat(authentication.getName()));
		}
		
		//Obtener el nombre de usuario de forma estática
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		//Validamos que el objeto "auth" no sea nulo
		if(auth != null) {
			logger.info("(Utilizando forma estática: SecurityContextHolder.getContext().getAuthentication()) Hola, usuario autenticado. Tu username es: ".concat(auth.getName()));
		}
		
		if(hasRole("ROLE_ADMIN")) {
			logger.info("Hola, ".concat(auth.getName()).concat(". Tienes acceso!"));
		} else {
			logger.info("Hola, ".concat(auth.getName()).concat(". NO Tienes acceso!"));
		}
		
		//Alternativa para chequear autorización: Método SecurityContextHolderAwareRequestWrapper
		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request, "");
		
		if(securityContext.isUserInRole("ROLE_ADMIN")) {
			logger.info("(Usando forma SecurityContextHolderAwareRequestWrapper) Hola, ".concat(auth.getName()).concat(". Tienes acceso!"));
		} else {
			logger.info("(Usando forma SecurityContextHolderAwareRequestWrapper) Hola, ".concat(auth.getName()).concat(". NO Tienes acceso!"));
		}
		
		//Alternativa para chequear autorización: Método HttpServletRequest (Forma nativa)
		if(request.isUserInRole("ROLE_ADMIN")) {
			logger.info("(Usando forma HttpServletRequest) Hola, ".concat(auth.getName()).concat(". Tienes acceso!"));
		} else {
			logger.info("(Usando forma HttpServletRequest) Hola, ".concat(auth.getName()).concat(". NO Tienes acceso!"));
		}
		
		//Elemento para especificar el número de clientes por página
		Pageable pageRequest = PageRequest.of(page, 4);
		
		//Obtenemos los clientes
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		
		//Inicializamos nuestro paginador con la vista y los clientes
		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);

		//Pasamos los datos a la vista
		model.addAttribute("titulo", messageSource.getMessage("text.cliente.listar.titulo", null, locale));
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);
		
		//Regresamos la vista
		return "listar";
	}
	
	//Método para mostrar el formulario de clientes
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/form")
	public String crear(Map<String, Object> model, Locale locale) {
		//Inicializamos un nuevo cliente sin datos
		Cliente cliente = new Cliente();
		
		//Pasamos los datos a la vista
		model.put("cliente", cliente);
		model.put("titulo", messageSource.getMessage("text.cliente.form.titulo.crear", null, locale));
		
		//Regresamos la vista
		return "form";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash, Locale locale) {
		
		//Inicializamos un cliente sin datos
		Cliente cliente = null;
		
		//Validamos que el id de un cliente sea mayor a 0
		if (id > 0) {
			
			//Buscamos al cliente
			cliente = clienteService.findOne(id);
			
			//Validamos que el cliente no sea nulo y que exista en la BBDD
			if (cliente == null) {
				flash.addFlashAttribute("error", messageSource.getMessage("text.cliente.flash.db.error", null, locale));
				return "redirect:/listar";
			}
		} else {
			flash.addFlashAttribute("error", messageSource.getMessage("text.cliente.flash.id.error", null, locale));
			return "redirect:/listar";
		}
		//Pasamos los datos a la vista
		model.put("cliente", cliente);
		model.put("titulo", messageSource.getMessage("text.cliente.form.titulo.editar", null, locale));
		
		//Regresamos la vista
		return "form";
	}
	
	//Método para crear / editar / guardar un cliente
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status, Locale locale) {
		
		//Validamos si el formulario tiene algún error
		if (result.hasErrors()) {
			model.addAttribute("titulo", messageSource.getMessage("text.cliente.form.titulo", null, locale));
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
			flash.addFlashAttribute("info", messageSource.getMessage("text.cliente.flash.foto.subir.success", null, locale) + "'" + uniqueFilename + "'");
			
			//Coloca la foto almacenada en la variable "uniqueFilename" al cliente actual
			cliente.setFoto(uniqueFilename);

		}
		
		//Mensaje - Indica que el cliente fue editado (si el id no es nulo) o creado (si el id es nulo) con éxito
		String mensajeFlash = (cliente.getId() != null) ? messageSource.getMessage("text.cliente.flash.editar.success", null, locale) : messageSource.getMessage("text.cliente.flash.crear.success", null, locale);
		
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
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash, Locale locale) {
		//Validamos si el id del cliente es mayor a 0
		if (id > 0) {
			//Buscamos al cliente
			Cliente cliente = clienteService.findOne(id);
			
			//Eliminamos al cliente
			clienteService.delete(id);
			
			//Mensaje - Indicamos que el cliente fue eliminado
			flash.addFlashAttribute("success", messageSource.getMessage("text.cliente.flash.eliminar.success", null, locale));
			
			//Validamos si el se borró la foto del cliente (en caso de que tuviera)
			if (uploadFileService.delete(cliente.getFoto())) {
				//Mensaje - Indicamos que la foto del cliente fue eliminada
				String mensajeFotoEliminar = String.format(messageSource.getMessage("text.cliente.flash.foto.eliminar.success", null, locale), cliente.getFoto());
				flash.addFlashAttribute("info", mensajeFotoEliminar);
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
	
	//Método para validar si el usuario tiene un role determinado
	private boolean hasRole(String role) {
		
		//Obtenemos el SecurityContext
		SecurityContext context = SecurityContextHolder.getContext();
		
		//Si el SecurityContext es nulo, el usuario no tiene ningún role
		if(context == null) {
			return false;
		}
		
		//Obtenemos la autenticación
		Authentication auth = context.getAuthentication();
		
		//Si no hay ninguna autenticación, el usuario no tiene ningún role
		if(auth == null) {
			return false;
		}
		
		//Toda clase role o que represente un role tiene que implementar la interfaz "GrantedAuthority"
		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
		
		//Forma simplificada de la validación: El usuario tiene un role
		return authorities.contains(new SimpleGrantedAuthority(role));
	}

}

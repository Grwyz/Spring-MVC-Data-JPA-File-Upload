package com.miprimerapagina.springboot.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miprimerapagina.springboot.app.models.service.IClienteService;
import com.miprimerapagina.springboot.app.view.xml.ClienteList;

@RestController
@RequestMapping("/api/clientes")
public class ClienteRestController {
	
	@Autowired
	private IClienteService clienteService;

	// REST usando la anotación "@ResponseBody" sobre el método handler
	@GetMapping(value = "/listar")
	public ClienteList listar() {
		return new ClienteList(clienteService.findAll());
	}

}

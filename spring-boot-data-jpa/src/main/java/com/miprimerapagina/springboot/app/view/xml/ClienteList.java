package com.miprimerapagina.springboot.app.view.xml;

import java.util.List;

import com.miprimerapagina.springboot.app.models.entity.Cliente;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="clientesList")
public class ClienteList {

	@XmlElement(name="cliente")
	public List<Cliente> clientes;
	
	public ClienteList() {
	}

	public ClienteList(List<Cliente> clientes) {
		super();
		this.clientes = clientes;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}
	
}

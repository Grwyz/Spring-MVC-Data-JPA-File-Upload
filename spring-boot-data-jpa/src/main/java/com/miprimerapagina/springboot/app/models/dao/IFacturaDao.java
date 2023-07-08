package com.miprimerapagina.springboot.app.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.miprimerapagina.springboot.app.models.entity.Factura;

public interface IFacturaDao extends CrudRepository<Factura, Long>{
	
}

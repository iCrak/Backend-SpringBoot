package com.bolsadeideas.springboot.backend.apirest.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bolsadeideas.springboot.backend.apirest.entity.Cliente;
import com.bolsadeideas.springboot.backend.apirest.entity.Region;
import com.bolsadeideas.springboot.backend.apirest.models.dao.IClienteDao;

@Service
public class ClienteServiceImpl implements IClienteService {

	//Inyeccion
	@Autowired
	private IClienteDao clientedao;
	
	@Override
	@Transactional(readOnly = true) //No es necesario por que los cliente Repository ya contiene Transactional
	public List<Cliente> findAll() {
		return (List<Cliente>)clientedao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Cliente> findAll(Pageable pageable) {
		return clientedao.findAll(pageable);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Cliente findById(Long id) {
		return clientedao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Cliente save(Cliente cliente) {
		return clientedao.save(cliente);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		clientedao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Region> findAllRegiones() {		
		return clientedao.findAllRegiones();
	}

	

}

package com.bolsadeideas.springboot.backend.apirest.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bolsadeideas.springboot.backend.apirest.entity.Cliente;
import com.bolsadeideas.springboot.backend.apirest.services.IClienteService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {
	
	@Autowired
	private IClienteService clienteservice;
	
	@GetMapping("/clientes")
	public List<Cliente> index(){
		return clienteservice.findAll();					
	}
	
	@GetMapping("/clientes/{id}")
	//@ResponseStatus(HttpStatus.OK) retorna 200
	public Cliente show(@PathVariable Long id) {
		return clienteservice.findById(id);
	}
	
	@PostMapping("/clientes")
	@ResponseStatus(HttpStatus.CREATED)
	public Cliente create(@RequestBody Cliente cliente) {
		return clienteservice.save(cliente);
	}
	
	@PutMapping("/clientes/{id}")
	public Cliente update(@RequestBody Cliente cliente,@PathVariable Long id) {
		Cliente clienteactual= clienteservice.findById(id);
		
		clienteactual.setNombre(cliente.getNombre());
		clienteactual.setApellido(cliente.getApellido());		
		clienteactual.setEmail(cliente.getEmail());
		
		return clienteservice.save(clienteactual);
	}
	
	@DeleteMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		clienteservice.delete(id);
	}
	
}

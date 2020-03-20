package com.bolsadeideas.springboot.backend.apirest.controllers;



import java.io.IOException;
import java.net.MalformedURLException;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

import javax.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bolsadeideas.springboot.backend.apirest.entity.Cliente;
import com.bolsadeideas.springboot.backend.apirest.services.IClienteService;
import com.bolsadeideas.springboot.backend.apirest.services.IUploadFileService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {
	
	@Autowired
	private IClienteService clienteservice;
	
	@Autowired
	private IUploadFileService uploadService;
	
	
	
	@GetMapping("/clientes")
	public List<Cliente> index(){
		return clienteservice.findAll();					
	}
	
	@GetMapping("/clientes/page/{page}")
	public Page<Cliente> index(@PathVariable Integer page){
		Pageable pageable=PageRequest.of(page, 8);
		return clienteservice.findAll(pageable);					
	}
	
	@GetMapping("/clientes/{id}")
	//@ResponseStatus(HttpStatus.OK) retorna 200
	public ResponseEntity<?> show(@PathVariable Long id) {
		Cliente cliente=null;
		Map<String,Object> response=new HashMap<>();
		try {
			cliente = clienteservice.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje","Error al realizar la consulta en la consulta en la base de datos");
			response.put("error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return  new  ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(cliente ==null) {
			response.put("mensaje","El cliente ID: " .concat(id.toString().concat(" no existe en la base de datos!")));
			return  new  ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}		
		return new ResponseEntity<Cliente>(cliente,HttpStatus.OK); 
	}
	
	@PostMapping("/clientes")
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente,BindingResult result) {		
		Cliente clientNew = null;
		Map<String,Object> response=new HashMap<>();
		
		/*if (result.hasErrors()) {
			
			List<String> errors=new ArrayList<>();			
		for (FieldError	 err: result.getFieldErrors()) {
			errors.add("El campo "+err.getField() + err.getDefaultMessage());
			}
			response.put("errors",errors);
			return  new  ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}*/
		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo "+ err.getField() +" " +err.getDefaultMessage())
					.collect(Collectors.toList());
						response.put("errors",errors);
						return  new  ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
				
		try {
			clientNew = clienteservice.save(cliente);
		} catch (DataAccessException e) {
			response.put("mensaje","Error al realizar el insert en la base de datos");
			response.put("error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return  new  ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El cliente ha sido creado con exito!");
		response.put("cliente", clientNew);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente,BindingResult result,@PathVariable Long id) {
		Cliente clienteactual = clienteservice.findById(id);
		
		Cliente clienteupdate = null;
		
		Map<String,Object> response=new HashMap<>();
		
		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo "+ err.getField() +" " +err.getDefaultMessage())
					.collect(Collectors.toList());
						response.put("errors",errors);
						return  new  ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		if(clienteactual ==null) {
			response.put("mensaje","Error: No se pudo editar, el cliente ID: " .concat(id.toString().concat(" no existe en la base de datos!")));
			return  new  ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}		
		try {
			clienteactual.setNombre(cliente.getNombre());
			clienteactual.setApellido(cliente.getApellido());		
			clienteactual.setEmail(cliente.getEmail());
			clienteactual.setCreaAt(cliente.getCreaAt());
			clienteupdate = clienteservice.save(clienteactual);			
		} catch (DataAccessException e) {
			response.put("mensaje","Error al realizar el actualizar en la base de datos");
			response.put("error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return  new  ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El cliente ha sido actualizado con exito!");
		response.put("cliente", clienteupdate);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String,Object> response=new HashMap<>();
		try {
			Cliente cliente=clienteservice.findById(id);
			String nombreFotoAnterior = cliente.getFoto();
			uploadService.eliminar(nombreFotoAnterior);			
			clienteservice.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje","Error al eliminar en la base de datos");
			response.put("error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return  new  ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje","El cliente eliminado con exito!");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	@PostMapping("/clientes/upload")
	public ResponseEntity<?> upload(@RequestParam("archivo") MultipartFile archivo,@RequestParam("id") Long id){
		Map<String,Object> response=new HashMap<>();
		
		Cliente cliente= clienteservice.findById(id);
		if (!archivo.isEmpty()) {			
			String nombreArchivo = null;
			try {
				nombreArchivo=uploadService.copiar(archivo);
			} catch (IOException e) {
				response.put("mensaje","Error al subir la imagen del cliente ");
				response.put("error",e.getMessage().concat(": ").concat(e.getCause().getMessage()));
				return  new  ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			String nombreFotoAnterior = cliente.getFoto();
			uploadService.eliminar(nombreFotoAnterior);
			
			cliente.setFoto(nombreArchivo);
			clienteservice.save(cliente);
			response.put("cliente", cliente);
			response.put("mensaje","Has subido correctamente la imagen: "+ nombreArchivo);
		}		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@GetMapping("/uploads/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto){
		
		Resource recurso =null;
		try {
			recurso= uploadService.cargar(nombreFoto);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
				
		HttpHeaders cabecera= new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+recurso.getFilename()+"\"");
		
		return new ResponseEntity<Resource>(recurso,cabecera,HttpStatus.OK);
	}
}

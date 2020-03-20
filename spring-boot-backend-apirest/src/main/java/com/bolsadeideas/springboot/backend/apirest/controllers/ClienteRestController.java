package com.bolsadeideas.springboot.backend.apirest.controllers;


import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {
	
	@Autowired
	private IClienteService clienteservice;
	
	private final Logger log= LoggerFactory.getLogger(ClienteRestController.class);
	
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
			
			if (nombreFotoAnterior !=null && nombreFotoAnterior.length()>0) {
				Path rutaFotoAnterior = Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
				File archivoFotoAnterior = rutaFotoAnterior.toFile();
				if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
					archivoFotoAnterior.delete();
				}
			}
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
			String prenombreArchivo =archivo.getOriginalFilename().replace(" ", "");
			String nombreArchivo = UUID.randomUUID().toString()+"_"+ prenombreArchivo;
			Path rutaAchivo = Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
			log.info(rutaAchivo.toString());
			try {
				Files.copy(archivo.getInputStream(), rutaAchivo);
			} catch (Exception e) {
				response.put("mensaje","Error al subir la imagen del cliente "+ prenombreArchivo);
				response.put("error",e.getMessage().concat(": ").concat(e.getCause().getMessage()));
				return  new  ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			String nombreFotoAnterior = cliente.getFoto();
			
			if (nombreFotoAnterior !=null && nombreFotoAnterior.length()>0) {
				Path rutaFotoAnterior = Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
				File archivoFotoAnterior = rutaFotoAnterior.toFile();
				if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
					archivoFotoAnterior.delete();
				}
			}
			
			cliente.setFoto(nombreArchivo);
			clienteservice.save(cliente);
			response.put("cliente", cliente);
			response.put("mensaje","Has subido correctamente la imagen: "+ prenombreArchivo);
		}		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@GetMapping("/uploads/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto){
		
		Path rutaAchivo = Paths.get("uploads").resolve(nombreFoto).toAbsolutePath();
		log.info(rutaAchivo.toString());
		Resource recurso =null;
		
		try {
			recurso = new UrlResource(rutaAchivo.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}		
		
		if (!recurso.exists() && !recurso.isReadable()) {
			rutaAchivo = Paths.get("src/main/resources/static/images").resolve("no-usuario.png").toAbsolutePath();
			try {
				recurso = new UrlResource(rutaAchivo.toUri());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}		
			log.error("Error no se pudo cargar la imagen: "+ nombreFoto);
		}
		HttpHeaders cabecera= new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+recurso.getFilename()+"\"");
		
		return new ResponseEntity<Resource>(recurso,cabecera,HttpStatus.OK);
	}
}

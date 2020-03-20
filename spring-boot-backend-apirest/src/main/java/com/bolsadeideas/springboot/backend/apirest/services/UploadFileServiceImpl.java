package com.bolsadeideas.springboot.backend.apirest.services;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileServiceImpl implements IUploadFileService{

	private final Logger log= LoggerFactory.getLogger(UploadFileServiceImpl.class);
	private final static String DIRECTORIO_UPLOAD= "uploads";
	
	
	@Override
	public Resource cargar(String nombreFoto) throws MalformedURLException {
		
		Path rutaAchivo = getPath(nombreFoto);
		log.info(rutaAchivo.toString());
		Resource recurso = new UrlResource(rutaAchivo.toUri());
		
		if (!recurso.exists() && !recurso.isReadable()) {
			rutaAchivo = Paths.get("src/main/resources/static/images").resolve("no-usuario.png").toAbsolutePath();			
				recurso = new UrlResource(rutaAchivo.toUri());			
			log.error("Error no se pudo cargar la imagen: "+ nombreFoto);
		}
		return recurso;
	}

	@Override
	public String copiar(MultipartFile archivo) throws IOException {
		
		String prenombreArchivo =archivo.getOriginalFilename().replace(" ", "");
		
		String nombreArchivo = UUID.randomUUID().toString()+"_"+ prenombreArchivo;
		
		Path rutaAchivo = getPath(nombreArchivo);
		log.info(rutaAchivo.toString());	
		
		Files.copy(archivo.getInputStream(), rutaAchivo);
		
		return nombreArchivo;
	}

	@Override
	public boolean eliminar(String nombreFoto) {
		if (nombreFoto !=null && nombreFoto.length()>0) {
			Path rutaFotoAnterior = Paths.get("uploads").resolve(nombreFoto).toAbsolutePath();
			File archivoFotoAnterior = rutaFotoAnterior.toFile();
			if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
				archivoFotoAnterior.delete();
				return true;
			}
		}
		return false;
	}

	@Override
	public Path getPath(String nombreFoto) {		 
		return Paths.get(DIRECTORIO_UPLOAD).resolve(nombreFoto).toAbsolutePath();
	}

}

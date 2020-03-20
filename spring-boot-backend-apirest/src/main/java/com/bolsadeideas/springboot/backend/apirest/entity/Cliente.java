package com.bolsadeideas.springboot.backend.apirest.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
//import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;



import lombok.Data;

@Data
@Entity
@Table(name = "clientes")
public class Cliente implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty(message = "no puede estar vacio")
	@Size(min=4,max=12 , message = "el tamaño tiene que estar entre 4 y 12")
	@Column(nullable = false)
	private String nombre;
	
	@NotEmpty(message = "no puede estar vacio")
	private String apellido;
	
	@NotEmpty(message = "no puede estar vacio")
	@Email(message = "no es una formato de correo")
	@Column(nullable = false,unique = false)
	private String email;
	
	@NotNull(message = "no puede estar vacio")
	@Column(name = "creat_at")
	@Temporal(TemporalType.DATE)
	private Date creaAt;
	
	private String foto;
	
	/*@PrePersist
	public void prePersist() {
		creaAt = new Date();
	}*/
}

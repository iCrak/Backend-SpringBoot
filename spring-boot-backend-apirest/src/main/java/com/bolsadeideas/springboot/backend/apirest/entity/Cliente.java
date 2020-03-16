package com.bolsadeideas.springboot.backend.apirest.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
@Entity
@Table(name = "clientes")
public class Cliente implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//@Column(length = 250,nullable = true,unique = true)
	private String nombre;
	
	private String apellido;
	private String email;
	
	@Column(name = "creat_at")
	@Temporal(TemporalType.DATE)
	private Date creaAt;
	
	@PrePersist
	public void prePersist() {
		creaAt = new Date();
	}
}

package br.com.sicredi.model;

import javax.persistence.Entity;

import edu.umd.cs.findbugs.annotations.NonNull;

@Entity
public class Associado extends EntidadeBase {

	@NonNull
	private String cpf;
	
	private String nome;
	

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
}

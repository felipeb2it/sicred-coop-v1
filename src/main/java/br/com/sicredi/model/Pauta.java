package br.com.sicredi.model;

import javax.persistence.Entity;

@Entity
public class Pauta extends EntidadeBase {

	private String assunto;
	private String resultado;
	
	
	public String getAssunto() {
		return assunto;
	}
	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}
	public String getResultado() {
		return resultado;
	}
	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
	
}

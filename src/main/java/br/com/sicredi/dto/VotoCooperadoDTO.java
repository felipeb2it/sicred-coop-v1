package br.com.sicredi.dto;

import edu.umd.cs.findbugs.annotations.NonNull;

public class VotoCooperadoDTO {
	
	@NonNull
	private Long idPauta;
	
	@NonNull
	private String cpf;
	
	@NonNull
	private String voto;
	
	public VotoCooperadoDTO() {
		
	}
	
	public VotoCooperadoDTO(Long idPauta, String cpf, String voto) {
		super();
		this.idPauta = idPauta;
		this.cpf = cpf;
		this.voto = voto;
	}
	public Long getIdPauta() {
		return idPauta;
	}
	public void setIdPauta(Long idPauta) {
		this.idPauta = idPauta;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getVoto() {
		return voto.toLowerCase();
	}
	public void setVoto(String voto) {
		this.voto = voto;
	}

}

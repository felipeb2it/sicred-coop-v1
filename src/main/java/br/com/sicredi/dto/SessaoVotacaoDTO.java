package br.com.sicredi.dto;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.core.annotation.Nullable;

public class SessaoVotacaoDTO {
	
	@NonNull
	private Long idPauta;
	
	@Nullable
	private Integer tempo;
	
	public SessaoVotacaoDTO() {
	}
	
	public SessaoVotacaoDTO(Long idPauta, Integer tempo) {
		super();
		this.idPauta = idPauta;
		this.tempo = tempo;
	}
	public Long getIdPauta() {
		return idPauta;
	}
	public void setIdPauta(Long idPauta) {
		this.idPauta = idPauta;
	}
	public Integer getTempo() {
		return tempo;
	}
	public void setTempo(Integer tempo) {
		this.tempo = tempo;
	} 
}

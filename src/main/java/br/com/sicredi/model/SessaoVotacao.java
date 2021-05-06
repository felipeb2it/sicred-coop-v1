package br.com.sicredi.model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class SessaoVotacao extends EntidadeBase {
	
	@OneToOne
	private Pauta pauta;
    
    @OneToOne
    private Associado associado;
    
    private String voto;

	public Pauta getPauta() {
		return pauta;
	}

	public void setPauta(Pauta pauta) {
		this.pauta = pauta;
	}

	public Associado getAssociado() {
		return associado;
	}

	public void setAssociado(Associado associado) {
		this.associado = associado;
	}

	public String getVoto() {
		return voto;
	}

	public void setVoto(String voto) {
		this.voto = voto;
	}
	
}

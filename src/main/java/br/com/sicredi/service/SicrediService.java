package br.com.sicredi.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.inject.Singleton;

import br.com.sicredi.model.Associado;
import br.com.sicredi.model.Pauta;
import br.com.sicredi.model.SessaoVotacao;
import br.com.sicredi.repository.AssociadoRepository;
import br.com.sicredi.repository.PautaRepository;
import br.com.sicredi.repository.SessaoVotacaoRepository;

@Singleton
public class SicrediService {

	@Inject
	AssociadoRepository associadoRepository;
	
	@Inject
	PautaRepository pautaRepository;
	
	@Inject
	SessaoVotacaoRepository sessaoVotacaoRepository;
	
	public void limparBanco() {
		sessaoVotacaoRepository.deleteAll();
		associadoRepository.deleteAll();
		pautaRepository.deleteAll();
	}
	
	public Associado insereAssociado(Associado associado) {
		return associadoRepository.save(associado);
	}
	
	public void limparSessoes() {
		sessaoVotacaoRepository.deleteAll();
	}
	
	public Associado findAssociadoByCpf(String cpf) {
		return associadoRepository.find(cpf);
	}
	
	public Optional<Associado> findAssociadoById(Long id) {
		return associadoRepository.findById(id);
	}
	
	public Pauta inserePauta(Pauta pauta) {
		return pautaRepository.save(pauta);
	}
	
	public Optional<Pauta> findPautaById(Long idPauta) {
		return pautaRepository.findById(idPauta);
	}
	
	public boolean findAssociadoByPauta(Associado associado, Pauta pauta) {
		return sessaoVotacaoRepository.existsByAssociadoAndPauta(associado, pauta);
	}
	
	public List<Associado> listAssociados(){
		return toList(associadoRepository.findAll());
	}
	
	public List<Pauta> listPautas(){
		return toList(pautaRepository.findAll());
	}
	
	public List<SessaoVotacao> listSessoes(){
		return toList(sessaoVotacaoRepository.findAll());
	}
	
	public static <T> List<T> toList(final Iterable<T> iterable) {
	    return StreamSupport.stream(iterable.spliterator(), false)
	                        .collect(Collectors.toList());
	}
	
}

package br.com.sicredi.service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import br.com.sicredi.model.Associado;
import br.com.sicredi.model.Pauta;
import br.com.sicredi.model.SessaoVotacao;
import br.com.sicredi.repository.AssociadoRepository;
import br.com.sicredi.repository.PautaRepository;
import br.com.sicredi.repository.SessaoVotacaoRepository;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.TaskScheduler;
import io.micronaut.session.Session;

@Singleton
public class SicrediService {

	@Inject
	AssociadoRepository associadoRepository;
	
	@Inject
	PautaRepository pautaRepository;
	
	@Inject
	SessaoVotacaoRepository sessaoVotacaoRepository;
	
	@Inject
	@Named(TaskExecutors.SCHEDULED)
	TaskScheduler taskScheduler;
	
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
	
	public void abrirSessao(Long tempoSessao, Session session, Pauta pauta) {
		taskScheduler.schedule(Duration.ofMinutes(tempoSessao), new Runnable() {
			public void run() {
				session.remove(pauta.getId().toString());
				String votos = contabilizar(pauta);
				pauta.setResultado(votos);
				pautaRepository.update(pauta);
			}
		});
	}
	
	public void votar(Pauta pauta, Associado associado, String voto) {
		SessaoVotacao sessao = new SessaoVotacao();
		sessao.setAssociado(associado);
		sessao.setPauta(pauta);
		sessao.setVoto(voto);
		sessaoVotacaoRepository.save(sessao);
	}
	
	public String contabilizar(Pauta pauta) {
		int positivo = sessaoVotacaoRepository.countByVotoAndPauta("sim", pauta).intValue();
		int negativo = sessaoVotacaoRepository.countByVotoAndPauta("não", pauta).intValue();
		int total = negativo + positivo;
		if(positivo > negativo) {
			return "Venceu o Sim, votos positivos " + positivo + ", votos negativos " + negativo + ", Total " + total;
		} else if(positivo == negativo) {
			return "Empate, votos positivos " + positivo + ", votos negativos " + negativo + ", Total " + total;
		} else if(total == 0) {
			return "Não houveram votos nesta pauta!";
		} else {
			return "Venceu o Não, votos positivos " + positivo + ", votos negativos " + negativo + ", Total " + total;
		}
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

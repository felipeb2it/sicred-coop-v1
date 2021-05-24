package br.com.sicredi.service;

import java.time.Duration;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sicredi.dto.SessaoVotacaoDTO;
import br.com.sicredi.dto.VotoCooperadoDTO;
import br.com.sicredi.model.Associado;
import br.com.sicredi.model.Pauta;
import br.com.sicredi.model.SessaoVotacao;
import br.com.sicredi.repository.PautaRepository;
import br.com.sicredi.repository.SessaoVotacaoRepository;
import io.micronaut.data.exceptions.EmptyResultException;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.TaskScheduler;
import io.micronaut.session.Session;

@Singleton
public class SicrediVotoService {
	
	private static final Logger LOG = LoggerFactory.getLogger(SicrediVotoService.class);

	@Inject
	SicrediService sicredService;
	
	@Inject
	PautaRepository pautaRepository;
	
	@Inject
	SessaoVotacaoRepository sessaoVotacaoRepository;
	
	@Inject
	@Named(TaskExecutors.SCHEDULED)
	TaskScheduler taskScheduler;
	
	
	public HttpResponse<Object> abrirSessao(Session session, @Body SessaoVotacaoDTO sessaoDto){
		Optional<Pauta> pauta = sicredService.findPautaById(sessaoDto.getIdPauta());
		
		if(pauta.isEmpty()) {
			throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Pauta não existe");
		}  else if(pauta.get().getResultado() != null) {
			throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Pauta ja foi contabilizada!");
		}
		
		if(session.get(sessaoDto.getIdPauta().toString()).isEmpty()) {
			session.put(sessaoDto.getIdPauta().toString(), pauta.get());
			abrirSessao(sessaoDto.getTempo() == null? 1L : sessaoDto.getTempo(), session, pauta.get());
		} else {
			throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "A sessão existe e ainda esta ativa!");
		}
		
		return HttpResponse.ok();
	}
	
	
	public HttpResponse<Object> receberVotos(Session session, @Body VotoCooperadoDTO votoCoop){
		Optional<Object> pautaSessao = session.get(votoCoop.getIdPauta().toString());
		Pauta pauta;
			
		Pauta pautaAtual = null;
		if(pautaSessao.isEmpty()) {
			Optional<Pauta> pautaRecover = sicredService.findPautaById(votoCoop.getIdPauta());
			if(pautaRecover.isEmpty() || pautaRecover.get().getResultado() != null) {
				throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Sessão fechada para esta pauta!");
			}
			pautaAtual = pautaRecover.get();
		}
		pauta = pautaAtual != null ? pautaAtual : (Pauta) pautaSessao.get();
			
		try {
			Associado associado = sicredService.findAssociadoByCpf(votoCoop.getCpf());
			boolean jaVotou = sicredService.findAssociadoByPauta(associado, pauta);
			
			if(jaVotou) {
				throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "O associado ja votou nesta pauta!");
			}
			
			if(associado.isAbleToVote() == null) {
				throw new HttpStatusException(HttpStatus.NOT_FOUND, "O CPF é inválido!");
			} else if(!associado.isAbleToVote()) {
				throw new HttpStatusException(HttpStatus.NOT_FOUND, "O CPF não é habilitado para votar!");
			}
			
			if(votoCoop.getVoto().equals("sim") || votoCoop.getVoto().equals("não")) {
				votar(pauta, associado, votoCoop.getVoto());
			} else {
				throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Voto inválido, vote sim ou não!");
			}
		
		}catch(EmptyResultException e) {
			throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Associado não existe!");
		}
		return HttpResponse.ok();
	}
	
	
	private void abrirSessao(Long tempoSessao, Session session, Pauta pauta) {
		taskScheduler.schedule(Duration.ofMinutes(tempoSessao), new Runnable() {
			public void run() {
				session.remove(pauta.getId().toString());
				String votos = contabilizar(pauta);
				pauta.setResultado(votos);
				pautaRepository.update(pauta);
				LOG.info("Pauta fechada e atualizada!");
			}
		});
	}
	
	private void votar(Pauta pauta, Associado associado, String voto) {
		SessaoVotacao sessao = new SessaoVotacao();
		sessao.setAssociado(associado);
		sessao.setPauta(pauta);
		sessao.setVoto(voto);
		sessaoVotacaoRepository.save(sessao);
	}
	
	private String contabilizar(Pauta pauta) {
		LOG.info("Contabilização de votos iniciada!");
		int positivo = sessaoVotacaoRepository.countByVotoAndPauta("sim", pauta).intValue();
		int negativo = sessaoVotacaoRepository.countByVotoAndPauta("não", pauta).intValue();
		int total = negativo + positivo;
		LOG.info("Contabilização de votos finalizada!");
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
	
}

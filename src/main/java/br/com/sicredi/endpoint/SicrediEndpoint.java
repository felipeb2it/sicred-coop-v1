package br.com.sicredi.endpoint;


import java.util.Optional;

import javax.inject.Inject;

import br.com.sicredi.dto.SessaoVotacaoDTO;
import br.com.sicredi.dto.VotoCooperadoDTO;
import br.com.sicredi.model.Associado;
import br.com.sicredi.model.Pauta;
import br.com.sicredi.service.SicrediService;
import br.com.sicredi.service.ValidaCpfService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.session.Session;

@Controller("/api")
public class SicrediEndpoint {
	
	@Inject
	SicrediService sicredService;
	
	@Inject
	ValidaCpfService validaCpfService;
	
	@Post(uri = "/pautas")
	public HttpResponse<Object> pautas(@Body Pauta pauta){
		return HttpResponse.ok(sicredService.inserePauta(pauta));
	}
	
	@Post(uri = "/sessoes-votacao")
	public HttpResponse<Object> abrirSessao(Session session, @Body SessaoVotacaoDTO sessaoDto){
		Optional<Pauta> pauta = sicredService.findPautaById(sessaoDto.getIdPauta());
		
		if(pauta.isEmpty()) {
			return HttpResponse.badRequest("Pauta não existe!");
		}  
		else if(pauta.get().getResultado() != null) {
			return HttpResponse.badRequest("Pauta ja foi contabilizada!");
		}
		
		if(session.get(sessaoDto.getIdPauta().toString()).isEmpty()) {
			session.put(sessaoDto.getIdPauta().toString(), pauta.get());
			sicredService.abrirSessao(sessaoDto.getTempo() == null? 1L : sessaoDto.getTempo(), session, pauta.get());
		} else {
			return HttpResponse.badRequest("A sessão existe e ainda esta ativa!");
		}
		
		return HttpResponse.ok();
	}
	
	
	@Post(uri = "/votos")
	public HttpResponse<Object> receberVotos(Session session, @Body VotoCooperadoDTO votoCoop){
		Optional<Object> pautaSessao = session.get(votoCoop.getIdPauta().toString());
		Pauta pauta;
		
		if(!validaCpfService.validaCpf(votoCoop.getCpf())) {
			return HttpResponse.badRequest("CPF inválido ao consultar https://user-info.herokuapp.com!");
		}
		
		Associado associado = sicredService.findAssociadoByCpf(votoCoop.getCpf());
		
		if(associado == null) {
			return HttpResponse.badRequest("Associado não existe!");
		}
		
		Pauta pautaAtual = null;
		if(pautaSessao.isEmpty()) {
			Optional<Pauta> pautaRecover = sicredService.findPautaById(votoCoop.getIdPauta());
			if(pautaRecover.isEmpty() || pautaRecover.get().getResultado() != null) {
				return HttpResponse.badRequest("Sessão fechada para esta pauta!");
			}
			pautaAtual = pautaRecover.get();
		}
		pauta = pautaAtual != null ? pautaAtual : (Pauta) pautaSessao.get();
		boolean jaVotou = sicredService.findAssociadoByPauta(associado, pauta);
		
		if(jaVotou) {
			return HttpResponse.badRequest("O associado ja votou nesta pauta!");
		}
		
		if(votoCoop.getVoto().equals("sim") || votoCoop.getVoto().equals("não")) {
			sicredService.votar(pauta, associado, votoCoop.getVoto());
		} else {
			return HttpResponse.badRequest("Voto inválido, vote sim ou não!");
		}
		return HttpResponse.ok();
	}

}

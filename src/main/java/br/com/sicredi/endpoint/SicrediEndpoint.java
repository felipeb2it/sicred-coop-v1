package br.com.sicredi.endpoint;


import javax.inject.Inject;

import br.com.sicredi.dto.SessaoVotacaoDTO;
import br.com.sicredi.dto.VotoCooperadoDTO;
import br.com.sicredi.model.Pauta;
import br.com.sicredi.service.SicrediService;
import br.com.sicredi.service.SicrediVotoService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.session.Session;

@Controller("/api")
public class SicrediEndpoint {
	
	@Inject
	SicrediService sicredService;
	
	@Inject
	SicrediVotoService votoService;
	
	@Post(uri = "/pautas")
	public HttpResponse<Object> pautas(@Body Pauta pauta){
		return HttpResponse.ok(sicredService.inserePauta(pauta));
	}
	
	@Post(uri = "/sessoes-votacao")
	public HttpResponse<Object> abrirSessao(Session session, @Body SessaoVotacaoDTO sessaoDto) throws HttpStatusException{
		return votoService.abrirSessao(session, sessaoDto);
	}
	
	@Post(uri = "/votos")
	public HttpResponse<Object> receberVotos(Session session, @Body VotoCooperadoDTO votoCoop){
		return votoService.receberVotos(session, votoCoop);
	}
	
}

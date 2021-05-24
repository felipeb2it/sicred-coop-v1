package br.com.sicredi.endpoint;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sicredi.model.Associado;
import br.com.sicredi.service.SicrediService;
import br.com.sicredi.service.ValidaCpfAsyncService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;

@Controller("/api")
public class SicredCrudEndpoint {
	
	private static final Logger LOG = LoggerFactory.getLogger(SicredCrudEndpoint.class);
	
	@Inject
	SicrediService sicredService;
	
	@Inject
	ValidaCpfAsyncService validaCpfAsyncService;
	
	@Delete(uri = "/todas-entidades")
	public HttpResponse<Object> limparBanco() {
		sicredService.limparBanco();
		return HttpResponse.ok();
	}

	@Post(uri = "/associados")
	public HttpResponse<Object> novoAssociado(@Body Associado associado){
		Associado associadoSalvo = sicredService.insereAssociado(associado);
		validaCpfAsyncService.consultarCpf(associadoSalvo.getCpf());
		LOG.info("Saiu da Validação ---------------------------------------------------------------------------");
		return HttpResponse.ok(associadoSalvo);
	}
	
	@Get(uri = "/associados")
	public HttpResponse<Object> listAssociados(){
		return HttpResponse.ok(sicredService.listAssociados());
	}
	
	@Get(uri = "/pautas")
	public HttpResponse<Object> listPautas(){
		return HttpResponse.ok(sicredService.listPautas());
	}
	
	@Get(uri = "/sessoes-votacao")
	public HttpResponse<Object> listSessoes(){
		return HttpResponse.ok(sicredService.listSessoes());
	}
	
	@Delete(uri = "/sessoes-votacao")
	public HttpResponse<Object> limparSessoes(){
		sicredService.limparSessoes();
		return HttpResponse.ok();
	}
	
}

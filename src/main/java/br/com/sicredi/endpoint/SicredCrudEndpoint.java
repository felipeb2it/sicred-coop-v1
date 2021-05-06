package br.com.sicredi.endpoint;

import javax.inject.Inject;

import br.com.sicredi.model.Associado;
import br.com.sicredi.service.SicrediService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;

@Controller("/api")
public class SicredCrudEndpoint {
	
	@Inject
	SicrediService sicredService;
	
	@Delete(uri = "/todas-entidades")
	public HttpResponse<Object> limparBanco() {
		sicredService.limparBanco();
		return HttpResponse.ok();
	}

	@Post(uri = "/associados")
	public HttpResponse<Object> novoAssociado(@Body Associado associado){
		return HttpResponse.ok(sicredService.insereAssociado(associado));
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

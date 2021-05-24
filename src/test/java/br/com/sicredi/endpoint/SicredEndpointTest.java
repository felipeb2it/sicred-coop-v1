package br.com.sicredi.endpoint;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import br.com.sicredi.dto.SessaoVotacaoDTO;
import br.com.sicredi.dto.VotoCooperadoDTO;
import br.com.sicredi.model.Associado;
import br.com.sicredi.model.Pauta;
import br.com.sicredi.service.SicrediService;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

@MicronautTest
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class SicredEndpointTest {
	
	@Inject
 	EmbeddedServer server; 

    @Inject
    @Client("/")
    HttpClient client; 
    
	@Inject
	SicrediService sicredService;
    
	@BeforeAll
	void inicializarBanco(){
		
		sicredService.limparBanco();
		Associado associado = new Associado();
    	associado.setCpf("07404404678");
    	associado.setNome("Paulo Oliveira Silva");
		sicredService.insereAssociado(associado);
	}
	
    @Test
    @Order(1)
    void testNovaPautaResponse() {

    	Pauta pauta = new Pauta();
    	pauta.setAssunto("Novo diretor X");
    	
    	Pauta response = client.toBlocking()
                .retrieve(HttpRequest.POST("/api/pautas", pauta), Pauta.class);
        Assertions.assertNotNull(response.getId());
    }

    @Test
    @Order(2)
    void testSessaoVotacaoResponse() {
    	Long idPauta = sicredService.listPautas().get(0).getId();

    	SessaoVotacaoDTO sessao = new SessaoVotacaoDTO(idPauta, 1);
    	HttpResponse<Object> response = client.toBlocking().exchange(HttpRequest.POST("/api/sessoes-votacao", sessao));
    	
        Assertions.assertEquals(200, response.code()); 
    }
    
}

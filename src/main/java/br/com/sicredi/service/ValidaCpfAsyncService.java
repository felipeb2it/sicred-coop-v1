package br.com.sicredi.service;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sicredi.dto.CpfResponseDTO;
import br.com.sicredi.model.Associado;
import br.com.sicredi.repository.AssociadoRepository;
import io.micronaut.data.exceptions.EmptyResultException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.runtime.http.scope.RequestScope;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;

@RequestScope
public class ValidaCpfAsyncService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ValidaCpfAsyncService.class);

    @Client("https://user-info.herokuapp.com")
	@Inject
	RxHttpClient httpClient;
	
	@Inject
	AssociadoRepository associadoRepository;
	
	private String ultimoCpf;
	
	private enum Status {
		ABLE_TO_VOTE,
		UNABLE_TO_VOTE;
	}
	
	 DisposableSingleObserver<CpfResponseDTO> ds = new DisposableSingleObserver<>() {
		@Override
		public void onSuccess(CpfResponseDTO t) {
			LOG.info("Retorno da consulta do CPF: " + ultimoCpf);
			if(Status.ABLE_TO_VOTE.toString().equals(t.getStatus())) {
				try {
					Associado associado = associadoRepository.find(ultimoCpf);
					associado.setAbleToVote(true);
					associadoRepository.update(associado);
					LOG.info(MessageFormat.format("CPF {0} Atualizado, ABLE_TO_VOTE!", ultimoCpf));
				}catch(EmptyResultException e) {
					LOG.warn(MessageFormat.format("CPF {0} não existe!", ultimoCpf));
					
				}
			} else {
				LOG.info(MessageFormat.format("CPF {0} é UNABLE_TO_VOTE!", ultimoCpf));
			}
			
		}

		@Override
		public void onError(Throwable e) {
			LOG.warn(MessageFormat.format("CPF {0} é inválido!", ultimoCpf));
		}
	    
	 };

    
    public void consultarCpf(String cpf) {
    	requestCpf(cpf);
    }

    private void requestCpf(String cpf) {
    	try {
    		ultimoCpf = cpf;
    		LOG.info(MessageFormat.format("Consulta CPF {0} enviado! ", cpf));
    		Single<CpfResponseDTO> source = httpClient.retrieve(HttpRequest.GET(urlCpf(cpf)), CpfResponseDTO.class).
    				timeout(2, TimeUnit.SECONDS).firstOrError();
    		CompositeDisposable composite = new CompositeDisposable();
    		composite.add(source.subscribeWith(ds));
    	} catch(NoSuchElementException notFound) {
    		throw new HttpStatusException(HttpStatus.NOT_FOUND, "CPF inválido!");
    	}
    }
    
    private String urlCpf(String cpf) {
		return UriBuilder.of("/users/{cpf}")
				.expand(Collections.singletonMap("cpf", cpf))
				.toString();
    }
	
}

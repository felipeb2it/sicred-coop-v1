package br.com.sicredi.service;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.uri.UriBuilder;

@Singleton
public class ValidaCpfService {

	@Client("https://user-info.herokuapp.com")
	@Inject
	RxHttpClient httpClient;
	
	public Boolean validaCpf(String cpf) {
		String uri = UriBuilder.of("/users/{cpf}")
				.expand(Collections.singletonMap("cpf", cpf))
				.toString();
		try {
			String result = httpClient.toBlocking().retrieve(uri);
			return result.equals("{\"status\":\"ABLE_TO_VOTE\"}") ? true : false;
		} catch(HttpClientResponseException exception) {
			return true;
		}
		
	}
	
}

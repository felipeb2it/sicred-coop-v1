package br.com.sicredi.repository;

import br.com.sicredi.model.Associado;
import br.com.sicredi.model.Pauta;
import br.com.sicredi.model.SessaoVotacao;
import io.micronaut.context.annotation.Executable;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface SessaoVotacaoRepository extends CrudRepository<SessaoVotacao, Long> {

	@Executable
	boolean existsByAssociadoAndPauta(Associado associado, Pauta pauta);
	
	Number countByVotoAndPauta(String voto, Pauta pauta);
	
}

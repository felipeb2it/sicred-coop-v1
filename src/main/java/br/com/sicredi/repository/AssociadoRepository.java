package br.com.sicredi.repository;

import br.com.sicredi.model.Associado;
import io.micronaut.context.annotation.Executable;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface AssociadoRepository extends CrudRepository<Associado, Long> {

	@Executable
	Associado find(String cpf);
	
}

package br.com.sicredi.repository;

import br.com.sicredi.model.Pauta;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface PautaRepository extends CrudRepository<Pauta, Long> {

}

package br.com.adv.veras_api.repository;

import br.com.adv.veras_api.entity.Intimacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IntimacaoRepository extends JpaRepository<Intimacao, Long> {

    boolean existsByComunicacaoDjenId(Long comunicacaoDjenId);

    Optional<Intimacao> findByComunicacaoDjenId(Long comunicacaoDjenId);
}

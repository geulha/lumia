package br.com.lumia.portal.repository;

import br.com.lumia.portal.domain.entity.Aprovacao;
import br.com.lumia.portal.domain.enums.DecisaoAprovacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AprovacaoRepository extends JpaRepository<Aprovacao, Integer> {

    List<Aprovacao> findByAprovadorIdAndDecisaoOrderByCriadoDesc(Integer aprovadorId, DecisaoAprovacao decisao);

    List<Aprovacao> findByAprovadorIdOrderByCriadoDesc(Integer aprovadorId);

    long countByAprovadorIdAndDecisao(Integer aprovadorId, DecisaoAprovacao decisao);

    Optional<Aprovacao> findByTicketIdAndDecisao(Integer ticketId, DecisaoAprovacao decisao);

    List<Aprovacao> findByTicketIdOrderByCriadoDesc(Integer ticketId);
}

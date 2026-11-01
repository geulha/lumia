package br.com.lumia.portal.repository;

import br.com.lumia.portal.domain.entity.InteracaoTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InteracaoTicketRepository extends JpaRepository<InteracaoTicket, Integer> {

    List<InteracaoTicket> findByTicketIdOrderByCriadoAsc(Integer ticketId);
}

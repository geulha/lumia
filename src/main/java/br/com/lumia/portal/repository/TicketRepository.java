package br.com.lumia.portal.repository;

import br.com.lumia.portal.domain.entity.Ticket;
import br.com.lumia.portal.domain.enums.StatusTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    List<Ticket> findByColaboradorIdOrderByCriadoDesc(Integer colaboradorId);

    List<Ticket> findByColaboradorIdAndStatusOrderByCriadoDesc(Integer colaboradorId, StatusTicket status);

    List<Ticket> findByAnalistaIdOrderByCriadoDesc(Integer analistaId);

    List<Ticket> findByStatusOrderByCriadoAsc(StatusTicket status);

    List<Ticket> findByStatusInOrderByCriadoDesc(List<StatusTicket> statuses);

    List<Ticket> findAllByOrderByCriadoDesc();

    long countByStatus(StatusTicket status);

    long countByStatusIn(List<StatusTicket> statuses);

    @Query("SELECT t FROM Ticket t WHERE t.analista.id = :analistaId ORDER BY t.criado DESC")
    List<Ticket> findMeuTrabalho(@Param("analistaId") Integer analistaId);

    @Query("SELECT t FROM Ticket t WHERE t.status = br.com.lumia.portal.domain.enums.StatusTicket.ABERTO ORDER BY t.criado ASC")
    List<Ticket> findTicketsEmAberto();

    @Query("SELECT t FROM Ticket t WHERE t.status IN (br.com.lumia.portal.domain.enums.StatusTicket.CONCLUIDO, br.com.lumia.portal.domain.enums.StatusTicket.REPROVADO) ORDER BY t.atualizado DESC")
    List<Ticket> findTicketsEncerrados();
}

package br.com.lumia.portal.repository;

import br.com.lumia.portal.domain.entity.RespostaFormulario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RespostaFormularioRepository extends JpaRepository<RespostaFormulario, Integer> {

    List<RespostaFormulario> findByTicketIdOrderByIdAsc(Integer ticketId);
}

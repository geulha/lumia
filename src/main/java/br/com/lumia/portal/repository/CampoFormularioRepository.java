package br.com.lumia.portal.repository;

import br.com.lumia.portal.domain.entity.CampoFormulario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampoFormularioRepository extends JpaRepository<CampoFormulario, Integer> {

    List<CampoFormulario> findByTipoSolicitacaoIdOrderByOrdemAsc(Integer tipoSolicitacaoId);
}

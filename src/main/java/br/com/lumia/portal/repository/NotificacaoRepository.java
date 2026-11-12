package br.com.lumia.portal.repository;

import br.com.lumia.portal.domain.entity.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Integer> {

    List<Notificacao> findByUsuarioIdOrderByCriadoDesc(Integer usuarioId);

    List<Notificacao> findTop8ByUsuarioIdOrderByCriadoDesc(Integer usuarioId);

    long countByUsuarioIdAndLidaFalse(Integer usuarioId);

    @Modifying
    @Query("UPDATE Notificacao n SET n.lida = true WHERE n.usuario.id = :usuarioId AND n.lida = false")
    void marcarTodasComoLidas(@Param("usuarioId") Integer usuarioId);
}

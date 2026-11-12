package br.com.lumia.portal.repository;

import br.com.lumia.portal.domain.entity.TipoSolicitacao;
import br.com.lumia.portal.domain.enums.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoSolicitacaoRepository extends JpaRepository<TipoSolicitacao, Integer> {

    List<TipoSolicitacao> findByAtivoTrueOrderByNomeAsc();

    List<TipoSolicitacao> findAllByOrderByNomeAsc();

    List<TipoSolicitacao> findByAtivoTrueAndCategoriaIgnoreCaseOrderByNomeAsc(String categoria);

    @Query("SELECT DISTINCT t.categoria FROM TipoSolicitacao t WHERE t.ativo = true AND t.categoria IS NOT NULL ORDER BY t.categoria ASC")
    List<String> findDistinctCategorias();

    @Query("SELECT t FROM TipoSolicitacao t WHERE t.ativo = true AND " +
           "(t.visivelParaPerfil IS NULL OR t.visivelParaPerfil = :perfil) AND " +
           "(t.visivelParaPosto IS NULL OR t.visivelParaPosto = '' OR LOWER(t.visivelParaPosto) = LOWER(:posto)) " +
           "ORDER BY t.nome ASC")
    List<TipoSolicitacao> findDisponiveisParaUsuario(@Param("perfil") Perfil perfil, @Param("posto") String posto);

    @Query("SELECT t FROM TipoSolicitacao t WHERE t.ativo = true AND " +
           "(t.visivelParaPerfil IS NULL OR t.visivelParaPerfil = :perfil) AND " +
           "(t.visivelParaPosto IS NULL OR t.visivelParaPosto = '' OR LOWER(t.visivelParaPosto) = LOWER(:posto)) AND " +
           "LOWER(t.categoria) = LOWER(:categoria) " +
           "ORDER BY t.nome ASC")
    List<TipoSolicitacao> findDisponiveisParaUsuarioECategoria(@Param("perfil") Perfil perfil, 
                                                              @Param("posto") String posto, 
                                                              @Param("categoria") String categoria);

    @Query("SELECT t FROM TipoSolicitacao t WHERE t.ativo = true AND " +
           "(t.visivelParaPerfil IS NULL OR t.visivelParaPerfil = :perfil) AND " +
           "(t.visivelParaPosto IS NULL OR t.visivelParaPosto = '' OR LOWER(t.visivelParaPosto) = LOWER(:posto)) AND " +
           "(LOWER(t.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(t.descricao) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(t.categoria) LIKE LOWER(CONCAT('%', :termo, '%'))) " +
           "ORDER BY t.nome ASC")
    List<TipoSolicitacao> buscarPorTermo(@Param("perfil") Perfil perfil, 
                                        @Param("posto") String posto, 
                                        @Param("termo") String termo);
}

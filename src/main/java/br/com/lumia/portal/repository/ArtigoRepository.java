package br.com.lumia.portal.repository;

import br.com.lumia.portal.domain.entity.Artigo;
import br.com.lumia.portal.domain.enums.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtigoRepository extends JpaRepository<Artigo, Integer> {

    List<Artigo> findByPublicadoTrueOrderByCriadoDesc();

    List<Artigo> findAllByOrderByCriadoDesc();

    List<Artigo> findByPublicadoTrueAndCategoriaIgnoreCaseOrderByCriadoDesc(String categoria);

    @Query("SELECT DISTINCT a.categoria FROM Artigo a WHERE a.publicado = true ORDER BY a.categoria ASC")
    List<String> findDistinctCategorias();

    @Query("SELECT a FROM Artigo a WHERE a.publicado = true AND " +
           "(:isRh = true OR ((a.visivelParaPerfil IS NULL OR a.visivelParaPerfil = :perfil) AND " +
           "(a.visivelParaPosto IS NULL OR a.visivelParaPosto = '' OR LOWER(a.visivelParaPosto) = LOWER(:posto)))) " +
           "ORDER BY a.criado DESC")
    List<Artigo> findDisponiveisParaUsuario(@Param("isRh") boolean isRh,
                                           @Param("perfil") Perfil perfil,
                                           @Param("posto") String posto);

    @Query("SELECT a FROM Artigo a WHERE a.publicado = true AND " +
           "(LOWER(a.titulo) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(a.conteudo) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(a.categoria) LIKE LOWER(CONCAT('%', :termo, '%'))) " +
           "ORDER BY a.criado DESC")
    List<Artigo> buscarPublicadosPorTermo(@Param("termo") String termo);
}

package br.com.lumia.portal.repository;

import br.com.lumia.portal.domain.entity.Artigo;
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
           "(LOWER(a.titulo) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(a.conteudo) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(a.categoria) LIKE LOWER(CONCAT('%', :termo, '%'))) " +
           "ORDER BY a.criado DESC")
    List<Artigo> buscarPublicadosPorTermo(@Param("termo") String termo);
}

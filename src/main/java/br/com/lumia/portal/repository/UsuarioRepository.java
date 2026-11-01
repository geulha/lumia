package br.com.lumia.portal.repository;

import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Usuario> findByPerfilOrderByNomeAsc(Perfil perfil);

    List<Usuario> findByAtivoTrueOrderByNomeAsc();

    List<Usuario> findAllByOrderByNomeAsc();
}

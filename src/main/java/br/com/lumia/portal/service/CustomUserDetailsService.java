package br.com.lumia.portal.service;

import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.repository.UsuarioRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("E-mail ou senha incorretos."));

        if (Boolean.TRUE.equals(usuario.getBloqueado())) {
            throw new LockedException("Seu acesso foi bloqueado pelo RH. Entre em contato com a equipe.");
        }

        if (Boolean.FALSE.equals(usuario.getAtivo())) {
            throw new DisabledException("Sua conta de acesso está inativa. Entre em contato com o RH.");
        }

        String role = "ROLE_" + usuario.getPerfil().name();

        return new User(
                usuario.getEmail(),
                usuario.getSenhaHash(),
                usuario.getAtivo(),
                true,
                true,
                !usuario.getBloqueado(),
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}

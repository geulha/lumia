package br.com.lumia.portal.config;

import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.repository.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;

    public AuthSuccessHandler(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Se o usuário precisa obrigatoriamente trocar a senha temporária no primeiro acesso
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailIgnoreCase(authentication.getName());
        if (usuarioOpt.isPresent() && Boolean.TRUE.equals(usuarioOpt.get().getPrimeiroAcesso())) {
            response.sendRedirect(request.getContextPath() + "/trocar-senha");
            return;
        }

        boolean isRh = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_RH"));

        if (isRh) {
            response.sendRedirect(request.getContextPath() + "/rh");
        } else {
            response.sendRedirect(request.getContextPath() + "/colaborador");
        }
    }
}

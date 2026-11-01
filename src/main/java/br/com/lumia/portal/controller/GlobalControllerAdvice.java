package br.com.lumia.portal.controller;

import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.DecisaoAprovacao;
import br.com.lumia.portal.repository.AprovacaoRepository;
import br.com.lumia.portal.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UsuarioRepository usuarioRepository;
    private final AprovacaoRepository aprovacaoRepository;

    public GlobalControllerAdvice(UsuarioRepository usuarioRepository, AprovacaoRepository aprovacaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.aprovacaoRepository = aprovacaoRepository;
    }

    @ModelAttribute("usuarioLogado")
    public Usuario getUsuarioLogado(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        String email = authentication.getName();
        return usuarioRepository.findByEmailIgnoreCase(email).orElse(null);
    }

    @ModelAttribute("pendenciasAprovacaoCount")
    public long getPendenciasAprovacaoCount(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return 0;
        }
        String email = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailIgnoreCase(email);
        if (usuarioOpt.isEmpty()) {
            return 0;
        }
        return aprovacaoRepository.countByAprovadorIdAndDecisao(usuarioOpt.get().getId(), DecisaoAprovacao.PENDENTE);
    }
}

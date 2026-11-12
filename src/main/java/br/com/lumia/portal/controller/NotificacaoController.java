package br.com.lumia.portal.controller;

import br.com.lumia.portal.domain.entity.Notificacao;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.repository.UsuarioRepository;
import br.com.lumia.portal.service.NotificacaoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;
    private final UsuarioRepository usuarioRepository;

    public NotificacaoController(NotificacaoService notificacaoService, UsuarioRepository usuarioRepository) {
        this.notificacaoService = notificacaoService;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario getUsuarioLogado(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Sessão expirada.");
        }
        return usuarioRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    }

    @GetMapping
    public String listarNotificacoes(Authentication authentication, Model model) {
        Usuario usuario = getUsuarioLogado(authentication);
        model.addAttribute("notificacoes", notificacaoService.listarTodas(usuario));
        return "notificacoes/lista";
    }

    @PostMapping("/{id}/ler")
    public String abrirNotificacao(@PathVariable("id") Integer id,
                                   Authentication authentication) {
        Usuario usuario = getUsuarioLogado(authentication);
        Notificacao notif = notificacaoService.marcarComoLida(id, usuario);
        if (notif != null && notif.getLink() != null && !notif.getLink().isBlank()) {
            return "redirect:" + notif.getLink();
        }
        return "redirect:/notificacoes";
    }

    @PostMapping("/ler-todas")
    public String marcarTodasLidas(Authentication authentication,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        Usuario usuario = getUsuarioLogado(authentication);
        notificacaoService.marcarTodasComoLidas(usuario);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Todas as notificações foram marcadas como lidas.");

        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }
        return "redirect:/";
    }
}

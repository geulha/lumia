package br.com.lumia.portal.controller;

import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.repository.UsuarioRepository;
import br.com.lumia.portal.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/")
    public String index(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            boolean isRh = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(a -> a.equals("ROLE_RH"));
            return isRh ? "redirect:/rh" : "redirect:/colaborador";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "erro", required = false) String erro,
                        @RequestParam(value = "logout", required = false) String logout,
                        Authentication authentication,
                        Model model) {
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/";
        }

        if (erro != null) {
            model.addAttribute("mensagemErro", "E-mail ou senha inválidos. Por favor, tente novamente.");
        }
        if (logout != null) {
            model.addAttribute("mensagemSucesso", "Você saiu da sua conta com segurança.");
        }

        return "login";
    }

    @GetMapping("/recuperar-senha")
    public String recuperarSenhaForm() {
        return "auth/recuperar-senha";
    }

    @PostMapping("/recuperar-senha")
    public String processarRecuperarSenha(@RequestParam("email") String email,
                                          Model model) {
        try {
            String novaSenha = usuarioService.recuperarSenha(email);
            model.addAttribute("sucesso", true);
            model.addAttribute("senhaGerada", novaSenha);
            model.addAttribute("emailRecuperado", email != null ? email.trim().toLowerCase() : "");
        } catch (IllegalArgumentException ex) {
            model.addAttribute("mensagemErro", ex.getMessage());
            model.addAttribute("emailPreenchido", email);
        }
        return "auth/recuperar-senha";
    }

    @GetMapping("/trocar-senha")
    public String trocarSenhaForm(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }
        return "auth/trocar-senha";
    }

    @PostMapping("/trocar-senha")
    public String processarTrocarSenha(@RequestParam("senhaAtual") String senhaAtual,
                                       @RequestParam("novaSenha") String novaSenha,
                                       @RequestParam("confirmacaoSenha") String confirmacaoSenha,
                                       Authentication authentication,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(authentication.getName()).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            usuarioService.alterarSenha(usuario, senhaAtual, novaSenha, confirmacaoSenha);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Senha alterada com sucesso! Bem-vindo(a) ao LUMIA.");
            return usuario.isRh() ? "redirect:/rh" : "redirect:/colaborador";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("mensagemErro", ex.getMessage());
            return "auth/trocar-senha";
        }
    }

    @GetMapping("/acesso-negado")
    public String acessoNegado(Model model) {
        model.addAttribute("titulo", "Acesso Não Autorizado");
        model.addAttribute("mensagem", "Você não possui permissão para acessar esta área ou funcionalidade.");
        return "error/403";
    }
}

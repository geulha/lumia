package br.com.lumia.portal.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

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

    @GetMapping("/acesso-negado")
    public String acessoNegado(Model model) {
        model.addAttribute("titulo", "Acesso Não Autorizado");
        model.addAttribute("mensagem", "Você não possui permissão para acessar esta área ou funcionalidade.");
        return "error/403";
    }
}

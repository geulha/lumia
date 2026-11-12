package br.com.lumia.portal.controller;

import br.com.lumia.portal.domain.entity.Aprovacao;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.DecisaoAprovacao;
import br.com.lumia.portal.repository.UsuarioRepository;
import br.com.lumia.portal.service.AprovacaoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/aprovacoes")
public class AprovacaoController {

    private final AprovacaoService aprovacaoService;
    private final UsuarioRepository usuarioRepository;

    public AprovacaoController(AprovacaoService aprovacaoService, UsuarioRepository usuarioRepository) {
        this.aprovacaoService = aprovacaoService;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario getUsuarioLogado(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Sessão expirada. Faça login novamente.");
        }
        return usuarioRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    }

    @GetMapping
    public String listarMinhasAprovacoes(Authentication authentication, Model model) {
        Usuario usuarioLogado = getUsuarioLogado(authentication);
        model.addAttribute("pendentes", aprovacaoService.listarPendentes(usuarioLogado));
        model.addAttribute("historico", aprovacaoService.listarHistoricoDecididas(usuarioLogado));
        return "aprovacoes/lista";
    }

    @GetMapping("/{id}")
    public String verDetalheAprovacao(@PathVariable("id") Integer id,
                                      Authentication authentication,
                                      Model model) {
        Usuario usuarioLogado = getUsuarioLogado(authentication);
        Aprovacao aprovacao = aprovacaoService.buscarPorId(id);

        if (!aprovacao.getAprovador().getId().equals(usuarioLogado.getId()) && !usuarioLogado.isRh()) {
            return "redirect:/acesso-negado";
        }

        model.addAttribute("aprovacao", aprovacao);
        model.addAttribute("ticket", aprovacao.getTicket());
        model.addAttribute("decisoes", DecisaoAprovacao.values());
        return "aprovacoes/detalhe";
    }

    @PostMapping("/{id}/decidir")
    public String decidirAprovacao(@PathVariable("id") Integer id,
                                   @RequestParam("decisao") DecisaoAprovacao decisao,
                                   @RequestParam(value = "comentario", required = false) String comentario,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        try {
            Usuario usuarioLogado = getUsuarioLogado(authentication);
            aprovacaoService.decidir(id, usuarioLogado, decisao, comentario);
            String acao = decisao == DecisaoAprovacao.APROVADO ? "aprovada e encaminhada ao RH" : "reprovada e encerrada";
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Solicitação " + acao + " com sucesso!");
            return "redirect:/aprovacoes";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao processar aprovação: " + ex.getMessage());
            return "redirect:/aprovacoes/" + id;
        }
    }
}

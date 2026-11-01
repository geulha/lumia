package br.com.lumia.portal.controller;

import br.com.lumia.portal.domain.entity.Aprovacao;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.DecisaoAprovacao;
import br.com.lumia.portal.service.AprovacaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/aprovacoes")
public class AprovacaoController {

    private final AprovacaoService aprovacaoService;

    public AprovacaoController(AprovacaoService aprovacaoService) {
        this.aprovacaoService = aprovacaoService;
    }

    @GetMapping
    public String listarMinhasAprovacoes(@ModelAttribute("usuarioLogado") Usuario usuarioLogado, Model model) {
        model.addAttribute("pendentes", aprovacaoService.listarPendentes(usuarioLogado));
        model.addAttribute("historico", aprovacaoService.listarHistoricoDecididas(usuarioLogado));
        return "aprovacoes/lista";
    }

    @GetMapping("/{id}")
    public String verDetalheAprovacao(@PathVariable("id") Integer id,
                                      @ModelAttribute("usuarioLogado") Usuario usuarioLogado,
                                      Model model) {
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
                                   @ModelAttribute("usuarioLogado") Usuario usuarioLogado,
                                   RedirectAttributes redirectAttributes) {
        try {
            aprovacaoService.decidir(id, usuarioLogado, decisao, comentario);
            String acao = decisao == DecisaoAprovacao.APROVADO ? "aprovada e encaminhada ao RH" : "reprovada e encerrada";
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Solicitação " + acao + " com sucesso!");
            return "redirect:/aprovacoes";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("mensagemErro", ex.getMessage());
            return "redirect:/aprovacoes/" + id;
        }
    }
}

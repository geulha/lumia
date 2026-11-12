package br.com.lumia.portal.controller;

import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.StatusTicket;
import br.com.lumia.portal.service.AprovacaoService;
import br.com.lumia.portal.service.TicketService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rh")
public class RhDashboardController {

    private final TicketService ticketService;
    private final AprovacaoService aprovacaoService;

    public RhDashboardController(TicketService ticketService, AprovacaoService aprovacaoService) {
        this.ticketService = ticketService;
        this.aprovacaoService = aprovacaoService;
    }

    @GetMapping
    public String dashboard(@ModelAttribute("usuarioLogado") Usuario usuarioLogado, Model model) {
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        // 4 Indicadores na faixa de gradiente calculados dinamicamente do banco
        // 1. Em aberto: sem analista atribuído
        model.addAttribute("contagemAbertos", ticketService.contarAbertosSemAnalista());

        // 2. Em andamento: tratados por aquele analista logado
        model.addAttribute("contagemAndamento", ticketService.contarAndamentoPorAnalista(usuarioLogado.getId()));

        // 3. Pendentes: pendentes de aprovação atribuídos para aquele analista
        model.addAttribute("contagemPendentes", ticketService.contarPendentesPorAnalista(usuarioLogado.getId()));

        // 4. Encerrados: histórico de chamados encerrados tratados por aquele analista
        model.addAttribute("contagemEncerrados", ticketService.contarEncerradosPorAnalista(usuarioLogado.getId()));

        // Grade "Meu Trabalho" atribuídos à analista de RH logada (fiel ao protótipo)
        model.addAttribute("meuTrabalho", ticketService.listarMeuTrabalho(usuarioLogado.getId()));

        return "rh/dashboard";
    }

    @GetMapping("/meu-trabalho")
    public String verMeuTrabalhoCompleto(@ModelAttribute("usuarioLogado") Usuario usuarioLogado, Model model) {
        if (usuarioLogado == null) {
            return "redirect:/login";
        }
        model.addAttribute("tickets", ticketService.listarMeuTrabalho(usuarioLogado.getId()));
        return "rh/tickets/meu-trabalho";
    }
}

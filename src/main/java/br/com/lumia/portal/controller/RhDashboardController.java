package br.com.lumia.portal.controller;

import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.StatusTicket;
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

    public RhDashboardController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public String dashboard(@ModelAttribute("usuarioLogado") Usuario usuarioLogado, Model model) {
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        // 4 Indicadores na faixa de gradiente calculados dinamicamente do banco
        model.addAttribute("contagemAbertos", ticketService.contarPorStatus(StatusTicket.ABERTO));
        model.addAttribute("contagemAndamento", ticketService.contarPorStatus(StatusTicket.EM_ANDAMENTO));
        model.addAttribute("contagemPendentes", ticketService.contarPorStatus(StatusTicket.PENDENTE));
        model.addAttribute("contagemEncerrados", ticketService.contarEncerrados());

        // Grade "Meu Trabalho" atribuídos à analista de RH logada (fiel ao protótipo)
        model.addAttribute("meuTrabalho", ticketService.listarMeuTrabalho(usuarioLogado.getId()));

        return "rh/dashboard";
    }

    @GetMapping("/meu-trabalho")
    public String verMeuTrabalhoCompleto(@ModelAttribute("usuarioLogado") Usuario usuarioLogado, Model model) {
        model.addAttribute("tickets", ticketService.listarMeuTrabalho(usuarioLogado.getId()));
        return "rh/tickets/meu-trabalho";
    }
}

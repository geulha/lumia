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
import org.springframework.web.bind.annotation.RequestParam;

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

        // 4 Indicadores na faixa de gradiente calculados dinamicamente do banco (contagem global)
        // 1. Em aberto: todos os tickets com status ABERTO no sistema
        model.addAttribute("contagemAbertos", ticketService.contarPorStatus(StatusTicket.ABERTO));

        // 2. Em andamento: todos os tickets com status EM_ANDAMENTO no sistema
        model.addAttribute("contagemAndamento", ticketService.contarPorStatus(StatusTicket.EM_ANDAMENTO));

        // 3. Pendentes: todos os tickets com status PENDENTE no sistema
        model.addAttribute("contagemPendentes", ticketService.contarPorStatus(StatusTicket.PENDENTE));

        // 4. Encerrados: todos os tickets finalizados (CONCLUIDO / REPROVADO) no sistema
        model.addAttribute("contagemEncerrados", ticketService.contarEncerrados());

        // Grade "Meu Trabalho" atribuídos à analista de RH logada (fiel ao protótipo)
        model.addAttribute("meuTrabalho", ticketService.listarMeuTrabalho(usuarioLogado.getId()));

        return "rh/dashboard";
    }

    @GetMapping("/meu-trabalho")
    public String verMeuTrabalhoCompleto(@RequestParam(value = "status", required = false) StatusTicket status,
                                         @ModelAttribute("usuarioLogado") Usuario usuarioLogado,
                                         Model model) {
        if (usuarioLogado == null) {
            return "redirect:/login";
        }
        model.addAttribute("tickets", ticketService.listarMeuTrabalho(usuarioLogado.getId(), status));
        model.addAttribute("statusSelecionado", status);
        model.addAttribute("todosStatus", StatusTicket.values());
        return "rh/tickets/meu-trabalho";
    }
}

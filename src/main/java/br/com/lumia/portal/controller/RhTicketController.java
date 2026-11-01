package br.com.lumia.portal.controller;

import br.com.lumia.portal.domain.entity.Ticket;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.StatusTicket;
import br.com.lumia.portal.service.TicketService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/rh/tickets")
public class RhTicketController {

    private final TicketService ticketService;

    public RhTicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/abertos")
    public String listarTicketsAbertos(@RequestParam(value = "termo", required = false) String termo,
                                       Model model) {
        List<Ticket> abertos = ticketService.listarAbertos();
        if (termo != null && !termo.isBlank()) {
            String termoLower = termo.trim().toLowerCase();
            abertos = abertos.stream()
                    .filter(t -> t.getCodigoFormatado().toLowerCase().contains(termoLower) ||
                            t.getColaborador().getNome().toLowerCase().contains(termoLower) ||
                            t.getTipoSolicitacao().getNome().toLowerCase().contains(termoLower))
                    .collect(Collectors.toList());
            model.addAttribute("termo", termo);
        }

        model.addAttribute("tickets", abertos);
        return "rh/tickets/abertos";
    }

    @GetMapping("/{id}")
    public String atendimentoTicket(@PathVariable("id") Integer id,
                                    @ModelAttribute("usuarioLogado") Usuario usuarioLogado,
                                    Model model) {
        Ticket ticket = ticketService.buscarPorId(id);
        model.addAttribute("ticket", ticket);
        model.addAttribute("statusPossiveis", StatusTicket.values());
        model.addAttribute("isAnalistaResponsavel", ticket.getAnalista() != null && ticket.getAnalista().getId().equals(usuarioLogado.getId()));
        return "rh/tickets/atendimento";
    }

    @PostMapping("/{id}/atribuir")
    public String atribuirAMim(@PathVariable("id") Integer id,
                               @ModelAttribute("usuarioLogado") Usuario usuarioLogado,
                               RedirectAttributes redirectAttributes) {
        ticketService.atribuirAnalista(id, usuarioLogado);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Ticket atribuído a você com sucesso! Status alterado para Em Andamento.");
        return "redirect:/rh/tickets/" + id;
    }

    @PostMapping("/{id}/comentar")
    public String adicionarDevolutiva(@PathVariable("id") Integer id,
                                      @RequestParam("conteudo") String conteudo,
                                      @ModelAttribute("usuarioLogado") Usuario usuarioLogado,
                                      RedirectAttributes redirectAttributes) {
        if (conteudo == null || conteudo.isBlank()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "A mensagem da devolutiva não pode ser vazia.");
            return "redirect:/rh/tickets/" + id;
        }

        ticketService.adicionarComentario(id, usuarioLogado, conteudo);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Devolutiva registrada com sucesso! O colaborador receberá esta atualização.");
        return "redirect:/rh/tickets/" + id;
    }

    @PostMapping("/{id}/status")
    public String atualizarStatus(@PathVariable("id") Integer id,
                                  @RequestParam("status") StatusTicket novoStatus,
                                  @RequestParam(value = "justificativa", required = false) String justificativa,
                                  @ModelAttribute("usuarioLogado") Usuario usuarioLogado,
                                  RedirectAttributes redirectAttributes) {

        ticketService.alterarStatus(id, usuarioLogado, novoStatus, justificativa);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Status do ticket alterado para '" + novoStatus.getDescricao() + "' com sucesso!");
        return "redirect:/rh/tickets/" + id;
    }

    @GetMapping("/{id}/anexos/{respostaId}")
    @ResponseBody
    public ResponseEntity<Resource> baixarAnexo(@PathVariable("id") Integer id,
                                                @PathVariable("respostaId") Integer respostaId,
                                                @ModelAttribute("usuarioLogado") Usuario usuarioLogado) {
        Resource resource = ticketService.carregarAnexo(id, respostaId, usuarioLogado);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}

package br.com.lumia.portal.controller;

import br.com.lumia.portal.domain.entity.Ticket;
import br.com.lumia.portal.domain.entity.TipoSolicitacao;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.StatusTicket;
import br.com.lumia.portal.service.TicketService;
import br.com.lumia.portal.service.TipoSolicitacaoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/colaborador/solicitacoes")
public class TicketColaboradorController {

    private final TicketService ticketService;
    private final TipoSolicitacaoService tipoSolicitacaoService;

    public TicketColaboradorController(TicketService ticketService,
                                       TipoSolicitacaoService tipoSolicitacaoService) {
        this.ticketService = ticketService;
        this.tipoSolicitacaoService = tipoSolicitacaoService;
    }

    @GetMapping
    public String listarMinhasSolicitacoes(@RequestParam(value = "status", required = false) StatusTicket status,
                                           @ModelAttribute("usuarioLogado") Usuario usuarioLogado,
                                           Model model) {
        model.addAttribute("solicitacoes", ticketService.listarPorColaborador(usuarioLogado.getId(), status));
        model.addAttribute("statusSelecionado", status);
        model.addAttribute("todosStatus", StatusTicket.values());
        return "colaborador/solicitacoes/lista";
    }

    @GetMapping("/nova/{tipoId}")
    public String abrirSolicitacaoForm(@PathVariable("tipoId") Integer tipoId,
                                       @ModelAttribute("usuarioLogado") Usuario usuarioLogado,
                                       Model model) {
        TipoSolicitacao tipo = tipoSolicitacaoService.buscarPorId(tipoId);
        model.addAttribute("tipo", tipo);
        model.addAttribute("colaborador", usuarioLogado);
        return "colaborador/solicitacoes/abrir";
    }

    @PostMapping("/nova/{tipoId}")
    public String submeterSolicitacao(@PathVariable("tipoId") Integer tipoId,
                                      @ModelAttribute("usuarioLogado") Usuario usuarioLogado,
                                      HttpServletRequest request,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {

        Map<String, String> respostasTexto = new HashMap<>();
        Map<String, MultipartFile> arquivos = new HashMap<>();

        // Extrai parâmetros normais
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (entry.getKey().startsWith("campo_") && entry.getValue().length > 0) {
                respostasTexto.put(entry.getKey(), entry.getValue()[0]);
            }
        }

        // Extrai arquivos se multipart
        if (request instanceof MultipartHttpServletRequest multipartRequest) {
            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
            for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
                if (entry.getKey().startsWith("campo_") && !entry.getValue().isEmpty()) {
                    arquivos.put(entry.getKey(), entry.getValue());
                }
            }
        }

        try {
            Ticket ticket = ticketService.abrirTicket(usuarioLogado, tipoId, respostasTexto, arquivos);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Chamado aberto com sucesso! Código gerado: " + ticket.getCodigoFormatado());
            return "redirect:/colaborador/solicitacoes/" + ticket.getId();
        } catch (IllegalArgumentException ex) {
            TipoSolicitacao tipo = tipoSolicitacaoService.buscarPorId(tipoId);
            model.addAttribute("tipo", tipo);
            model.addAttribute("colaborador", usuarioLogado);
            model.addAttribute("mensagemErro", ex.getMessage());
            model.addAttribute("valoresInformados", respostasTexto);
            return "colaborador/solicitacoes/abrir";
        }
    }

    @GetMapping("/{id}")
    public String verDetalhesSolicitacao(@PathVariable("id") Integer id,
                                         @ModelAttribute("usuarioLogado") Usuario usuarioLogado,
                                         Model model) {
        Ticket ticket = ticketService.buscarPorId(id);
        ticketService.validarAcessoTicket(ticket, usuarioLogado);

        model.addAttribute("ticket", ticket);
        return "colaborador/solicitacoes/detalhes";
    }

    @PostMapping("/{id}/comentar")
    public String adicionarComentario(@PathVariable("id") Integer id,
                                      @RequestParam("conteudo") String conteudo,
                                      @ModelAttribute("usuarioLogado") Usuario usuarioLogado,
                                      RedirectAttributes redirectAttributes) {
        if (conteudo == null || conteudo.isBlank()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "O comentário não pode ser vazio.");
            return "redirect:/colaborador/solicitacoes/" + id;
        }

        ticketService.adicionarComentario(id, usuarioLogado, conteudo);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Comentário registrado com sucesso no histórico!");
        return "redirect:/colaborador/solicitacoes/" + id;
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

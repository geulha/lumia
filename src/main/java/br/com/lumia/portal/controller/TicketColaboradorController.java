package br.com.lumia.portal.controller;

import br.com.lumia.portal.domain.entity.Ticket;
import br.com.lumia.portal.domain.entity.TipoSolicitacao;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.StatusTicket;
import br.com.lumia.portal.repository.UsuarioRepository;
import br.com.lumia.portal.service.TicketService;
import br.com.lumia.portal.service.TipoSolicitacaoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    private final UsuarioRepository usuarioRepository;

    public TicketColaboradorController(TicketService ticketService,
                                       TipoSolicitacaoService tipoSolicitacaoService,
                                       UsuarioRepository usuarioRepository) {
        this.ticketService = ticketService;
        this.tipoSolicitacaoService = tipoSolicitacaoService;
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
    public String listarMinhasSolicitacoes(@RequestParam(value = "status", required = false) StatusTicket status,
                                           Authentication authentication,
                                           Model model) {
        Usuario usuarioLogado = getUsuarioLogado(authentication);
        model.addAttribute("solicitacoes", ticketService.listarPorColaborador(usuarioLogado.getId(), status));
        model.addAttribute("statusSelecionado", status);
        model.addAttribute("todosStatus", StatusTicket.values());
        return "colaborador/solicitacoes/lista";
    }

    @GetMapping("/nova/{tipoId}")
    public String abrirSolicitacaoForm(@PathVariable("tipoId") Integer tipoId,
                                       Authentication authentication,
                                       Model model) {
        Usuario usuarioLogado = getUsuarioLogado(authentication);
        TipoSolicitacao tipo = tipoSolicitacaoService.buscarPorId(tipoId);
        model.addAttribute("tipo", tipo);
        model.addAttribute("colaborador", usuarioLogado);
        return "colaborador/solicitacoes/abrir";
    }

    @PostMapping("/nova/{tipoId}")
    public String submeterSolicitacao(@PathVariable("tipoId") Integer tipoId,
                                      Authentication authentication,
                                      HttpServletRequest request,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {

        Usuario usuarioLogado = getUsuarioLogado(authentication);
        Map<String, String> respostasTexto = new HashMap<>();
        Map<String, MultipartFile> arquivos = new HashMap<>();

        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (entry.getKey().startsWith("campo_") && entry.getValue().length > 0) {
                respostasTexto.put(entry.getKey(), entry.getValue()[0]);
            }
        }

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
                                         Authentication authentication,
                                         Model model) {
        Usuario usuarioLogado = getUsuarioLogado(authentication);
        Ticket ticket = ticketService.buscarPorId(id);
        ticketService.validarAcessoTicket(ticket, usuarioLogado);

        model.addAttribute("ticket", ticket);
        model.addAttribute("isFinalizado", ticket.isFinalizado());
        return "colaborador/solicitacoes/detalhes";
    }

    @PostMapping("/{id}/comentar")
    public String adicionarComentario(@PathVariable("id") Integer id,
                                      @RequestParam("conteudo") String conteudo,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {
        if (conteudo == null || conteudo.isBlank()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "O comentário não pode ser vazio.");
            return "redirect:/colaborador/solicitacoes/" + id;
        }

        try {
            Usuario usuarioLogado = getUsuarioLogado(authentication);
            ticketService.adicionarComentario(id, usuarioLogado, conteudo);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Comentário registrado com sucesso no histórico!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensagemErro", ex.getMessage());
        }
        return "redirect:/colaborador/solicitacoes/" + id;
    }

    @GetMapping("/{id}/anexos/{respostaId}")
    @ResponseBody
    public ResponseEntity<Resource> baixarAnexo(@PathVariable("id") Integer id,
                                                @PathVariable("respostaId") Integer respostaId,
                                                Authentication authentication) {
        Usuario usuarioLogado = getUsuarioLogado(authentication);
        Resource resource = ticketService.carregarAnexo(id, respostaId, usuarioLogado);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}

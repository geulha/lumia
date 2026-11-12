package br.com.lumia.portal.controller;

import br.com.lumia.portal.domain.entity.TipoSolicitacao;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.service.ArtigoService;
import br.com.lumia.portal.service.TicketService;
import br.com.lumia.portal.service.TipoSolicitacaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/colaborador")
public class ColaboradorController {

    private final TipoSolicitacaoService tipoSolicitacaoService;
    private final TicketService ticketService;
    private final ArtigoService artigoService;

    public ColaboradorController(TipoSolicitacaoService tipoSolicitacaoService,
                                 TicketService ticketService,
                                 ArtigoService artigoService) {
        this.tipoSolicitacaoService = tipoSolicitacaoService;
        this.ticketService = ticketService;
        this.artigoService = artigoService;
    }

    @GetMapping
    public String home(@ModelAttribute("usuarioLogado") Usuario usuarioLogado, Model model) {
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        // 4 Solicitações mais recentes do colaborador (fiel ao protótipo)
        model.addAttribute("minhasSolicitacoes", ticketService.listarRecentesColaborador(usuarioLogado.getId(), 4));

        // 4 Artigos da Base de Conhecimento mais recentes (fiel ao protótipo)
        model.addAttribute("artigosRecentes", artigoService.listarPublicadosRecentesParaUsuario(usuarioLogado, 4));

        return "colaborador/home";
    }

    @GetMapping("/formularios")
    public String listarFormularios(@RequestParam(value = "categoria", required = false) String categoria,
                                    @RequestParam(value = "termo", required = false) String termo,
                                    @ModelAttribute("usuarioLogado") Usuario usuarioLogado,
                                    Model model) {
        List<TipoSolicitacao> formularios;
        if (termo != null && !termo.isBlank()) {
            formularios = tipoSolicitacaoService.buscarPorTermo(usuarioLogado, termo);
            model.addAttribute("filtroTermo", termo);
        } else if (categoria != null && !categoria.isBlank()) {
            formularios = tipoSolicitacaoService.listarDisponiveisPorCategoria(usuarioLogado, categoria);
            model.addAttribute("filtroCategoria", categoria);
        } else {
            formularios = tipoSolicitacaoService.listarDisponiveisParaUsuario(usuarioLogado);
        }

        model.addAttribute("formularios", formularios);
        model.addAttribute("categorias", tipoSolicitacaoService.listarCategoriasDisponiveis());
        return "colaborador/formularios";
    }
}

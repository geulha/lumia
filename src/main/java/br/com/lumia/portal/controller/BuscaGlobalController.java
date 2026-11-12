package br.com.lumia.portal.controller;

import br.com.lumia.portal.domain.entity.Artigo;
import br.com.lumia.portal.domain.entity.TipoSolicitacao;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.repository.UsuarioRepository;
import br.com.lumia.portal.service.ArtigoService;
import br.com.lumia.portal.service.TipoSolicitacaoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BuscaGlobalController {

    private final TipoSolicitacaoService tipoSolicitacaoService;
    private final ArtigoService artigoService;
    private final UsuarioRepository usuarioRepository;

    public BuscaGlobalController(TipoSolicitacaoService tipoSolicitacaoService,
                                 ArtigoService artigoService,
                                 UsuarioRepository usuarioRepository) {
        this.tipoSolicitacaoService = tipoSolicitacaoService;
        this.artigoService = artigoService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/busca")
    public String buscaGlobal(@RequestParam(value = "termo", required = false) String termo,
                              Authentication authentication,
                              Model model) {
        Usuario usuarioLogado = null;
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            usuarioLogado = usuarioRepository.findByEmailIgnoreCase(authentication.getName()).orElse(null);
        }

        List<TipoSolicitacao> formularios;
        List<Artigo> artigos;

        if (termo != null && !termo.trim().isEmpty()) {
            String termoLimpo = termo.trim();
            formularios = tipoSolicitacaoService.buscarPorTermo(usuarioLogado, termoLimpo);
            artigos = artigoService.buscarPublicadosPorTermo(termoLimpo);
        } else {
            formularios = tipoSolicitacaoService.listarDisponiveisParaUsuario(usuarioLogado);
            artigos = artigoService.listarPublicados();
        }

        model.addAttribute("termo", termo != null ? termo.trim() : "");
        model.addAttribute("formularios", formularios);
        model.addAttribute("artigos", artigos);
        model.addAttribute("totalResultados", formularios.size() + artigos.size());

        return "busca";
    }
}

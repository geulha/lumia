package br.com.lumia.portal.controller;

import br.com.lumia.portal.domain.entity.Artigo;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.service.ArtigoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/artigos")
public class BaseConhecimentoController {

    private final ArtigoService artigoService;

    public BaseConhecimentoController(ArtigoService artigoService) {
        this.artigoService = artigoService;
    }

    @GetMapping
    public String listarArtigos(@RequestParam(value = "categoria", required = false) String categoria,
                                @RequestParam(value = "termo", required = false) String termo,
                                @ModelAttribute("usuarioLogado") Usuario usuarioLogado,
                                Model model) {
        List<Artigo> artigos;
        if (termo != null && !termo.isBlank()) {
            artigos = artigoService.buscarPublicadosPorTermo(usuarioLogado, termo);
            model.addAttribute("filtroTermo", termo);
        } else if (categoria != null && !categoria.isBlank()) {
            artigos = artigoService.buscarPublicadosPorCategoria(usuarioLogado, categoria);
            model.addAttribute("filtroCategoria", categoria);
        } else {
            artigos = artigoService.listarDisponiveisParaUsuario(usuarioLogado);
        }

        model.addAttribute("artigos", artigos);
        model.addAttribute("categorias", artigoService.listarCategorias());
        return "artigos/lista";
    }

    @GetMapping("/{id}")
    public String lerArtigo(@PathVariable("id") Integer id, Model model) {
        Artigo artigo = artigoService.buscarPorId(id);
        if (!Boolean.TRUE.equals(artigo.getPublicado())) {
            model.addAttribute("mensagemAlerta", "Este artigo ainda não foi publicado ou está em revisão.");
        }
        model.addAttribute("artigo", artigo);
        return "artigos/detalhe";
    }
}

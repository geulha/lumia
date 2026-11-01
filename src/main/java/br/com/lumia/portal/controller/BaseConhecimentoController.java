package br.com.lumia.portal.controller;

import br.com.lumia.portal.domain.entity.Artigo;
import br.com.lumia.portal.service.ArtigoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
                                Model model) {
        List<Artigo> artigos;
        if (termo != null && !termo.isBlank()) {
            artigos = artigoService.buscarPublicadosPorTermo(termo);
            model.addAttribute("filtroTermo", termo);
        } else if (categoria != null && !categoria.isBlank()) {
            artigos = artigoService.buscarPublicadosPorCategoria(categoria);
            model.addAttribute("filtroCategoria", categoria);
        } else {
            artigos = artigoService.listarPublicados();
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

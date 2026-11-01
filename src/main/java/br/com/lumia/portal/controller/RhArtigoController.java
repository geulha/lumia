package br.com.lumia.portal.controller;

import br.com.lumia.portal.domain.entity.Artigo;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.dto.ArtigoForm;
import br.com.lumia.portal.service.ArtigoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rh/gerenciar/base-conhecimento")
public class RhArtigoController {

    private final ArtigoService artigoService;

    public RhArtigoController(ArtigoService artigoService) {
        this.artigoService = artigoService;
    }

    @GetMapping
    public String listarArtigos(Model model) {
        model.addAttribute("artigos", artigoService.listarTodos());
        return "rh/artigos/lista";
    }

    @GetMapping("/novo")
    public String novoArtigoForm(Model model) {
        ArtigoForm form = new ArtigoForm();
        model.addAttribute("artigoForm", form);
        model.addAttribute("categorias", artigoService.listarCategorias());
        model.addAttribute("isEdicao", false);
        return "rh/artigos/formulario";
    }

    @PostMapping("/novo")
    public String criarArtigo(@Valid @ModelAttribute("artigoForm") ArtigoForm form,
                              BindingResult result,
                              @ModelAttribute("usuarioLogado") Usuario usuarioLogado,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", artigoService.listarCategorias());
            model.addAttribute("isEdicao", false);
            return "rh/artigos/formulario";
        }

        try {
            artigoService.criar(form, usuarioLogado);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Artigo cadastrado com sucesso na Base de Conhecimento!");
            return "redirect:/rh/gerenciar/base-conhecimento";
        } catch (Exception ex) {
            model.addAttribute("mensagemErro", "Erro ao cadastrar artigo: " + ex.getMessage());
            model.addAttribute("categorias", artigoService.listarCategorias());
            model.addAttribute("isEdicao", false);
            return "rh/artigos/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editarArtigoForm(@PathVariable("id") Integer id, Model model) {
        Artigo artigo = artigoService.buscarPorId(id);
        ArtigoForm form = new ArtigoForm();
        form.setId(artigo.getId());
        form.setTitulo(artigo.getTitulo());
        form.setConteudo(artigo.getConteudo());
        form.setCategoria(artigo.getCategoria());
        form.setPublicado(artigo.getPublicado());

        model.addAttribute("artigoForm", form);
        model.addAttribute("categorias", artigoService.listarCategorias());
        model.addAttribute("isEdicao", true);
        model.addAttribute("artigoTitulo", artigo.getTitulo());
        return "rh/artigos/formulario";
    }

    @PostMapping("/{id}/editar")
    public String atualizarArtigo(@PathVariable("id") Integer id,
                                  @Valid @ModelAttribute("artigoForm") ArtigoForm form,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", artigoService.listarCategorias());
            model.addAttribute("isEdicao", true);
            return "rh/artigos/formulario";
        }

        try {
            artigoService.atualizar(id, form);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Artigo atualizado com sucesso!");
            return "redirect:/rh/gerenciar/base-conhecimento";
        } catch (Exception ex) {
            model.addAttribute("mensagemErro", "Erro ao atualizar artigo: " + ex.getMessage());
            model.addAttribute("categorias", artigoService.listarCategorias());
            model.addAttribute("isEdicao", true);
            return "rh/artigos/formulario";
        }
    }

    @PostMapping("/{id}/toggle-publicado")
    public String alternarPublicacao(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        boolean publicado = artigoService.alternarPublicacao(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Status do artigo alterado para " + (publicado ? "Publicado" : "Despublicado (Rascunho)") + ".");
        return "redirect:/rh/gerenciar/base-conhecimento";
    }

    @PostMapping("/{id}/excluir")
    public String excluirArtigo(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            artigoService.excluir(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Artigo excluído com sucesso!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao excluir artigo: " + ex.getMessage());
        }
        return "redirect:/rh/gerenciar/base-conhecimento";
    }
}

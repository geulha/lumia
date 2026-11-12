package br.com.lumia.portal.controller;

import br.com.lumia.portal.domain.entity.TipoSolicitacao;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.Perfil;
import br.com.lumia.portal.domain.enums.TipoCampo;
import br.com.lumia.portal.dto.CampoFormularioForm;
import br.com.lumia.portal.dto.TipoSolicitacaoForm;
import br.com.lumia.portal.service.TipoSolicitacaoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rh/gerenciar/formularios")
public class RhFormularioController {

    private final TipoSolicitacaoService tipoSolicitacaoService;
    private final br.com.lumia.portal.repository.UsuarioRepository usuarioRepository;

    public RhFormularioController(TipoSolicitacaoService tipoSolicitacaoService,
                                  br.com.lumia.portal.repository.UsuarioRepository usuarioRepository) {
        this.tipoSolicitacaoService = tipoSolicitacaoService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String listarFormularios(Model model) {
        model.addAttribute("formularios", tipoSolicitacaoService.listarTodos());
        return "rh/formularios/lista";
    }

    @GetMapping("/novo")
    public String novoFormularioForm(Model model) {
        TipoSolicitacaoForm form = new TipoSolicitacaoForm();
        model.addAttribute("tipoForm", form);
        model.addAttribute("perfis", Perfil.values());
        model.addAttribute("categorias", tipoSolicitacaoService.listarCategoriasDisponiveis());
        model.addAttribute("isEdicao", false);
        return "rh/formularios/formulario";
    }

    @PostMapping("/novo")
    public String criarFormulario(@Valid @ModelAttribute("tipoForm") TipoSolicitacaoForm form,
                                  BindingResult result,
                                  org.springframework.security.core.Authentication authentication,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("perfis", Perfil.values());
            model.addAttribute("categorias", tipoSolicitacaoService.listarCategoriasDisponiveis());
            model.addAttribute("isEdicao", false);
            return "rh/formularios/formulario";
        }

        try {
            Usuario usuarioLogado = usuarioRepository.findByEmailIgnoreCase(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
            TipoSolicitacao criado = tipoSolicitacaoService.criar(form, usuarioLogado);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Formulário criado com sucesso! Agora defina os campos de preenchimento.");
            return "redirect:/rh/gerenciar/formularios/" + criado.getId() + "/campos";
        } catch (Exception ex) {
            model.addAttribute("mensagemErro", "Erro ao salvar formulário: " + ex.getMessage());
            model.addAttribute("perfis", Perfil.values());
            model.addAttribute("categorias", tipoSolicitacaoService.listarCategoriasDisponiveis());
            model.addAttribute("isEdicao", false);
            return "rh/formularios/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editarFormularioForm(@PathVariable("id") Integer id, Model model) {
        TipoSolicitacao tipo = tipoSolicitacaoService.buscarPorId(id);
        TipoSolicitacaoForm form = new TipoSolicitacaoForm();
        form.setId(tipo.getId());
        form.setNome(tipo.getNome());
        form.setDescricao(tipo.getDescricao());
        form.setCategoria(tipo.getCategoria());
        form.setRequerAprovacao(tipo.getRequerAprovacao());
        form.setVisivelParaPerfil(tipo.getVisivelParaPerfil());
        form.setVisivelParaPosto(tipo.getVisivelParaPosto());
        form.setAtivo(tipo.getAtivo());

        model.addAttribute("tipoForm", form);
        model.addAttribute("perfis", Perfil.values());
        model.addAttribute("categorias", tipoSolicitacaoService.listarCategoriasDisponiveis());
        model.addAttribute("isEdicao", true);
        model.addAttribute("formularioNome", tipo.getNome());
        return "rh/formularios/formulario";
    }

    @PostMapping("/{id}/editar")
    public String atualizarFormulario(@PathVariable("id") Integer id,
                                      @Valid @ModelAttribute("tipoForm") TipoSolicitacaoForm form,
                                      BindingResult result,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("perfis", Perfil.values());
            model.addAttribute("categorias", tipoSolicitacaoService.listarCategoriasDisponiveis());
            model.addAttribute("isEdicao", true);
            return "rh/formularios/formulario";
        }

        try {
            tipoSolicitacaoService.atualizar(id, form);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Configurações do formulário atualizadas com sucesso!");
            return "redirect:/rh/gerenciar/formularios";
        } catch (Exception ex) {
            model.addAttribute("mensagemErro", "Erro ao atualizar: " + ex.getMessage());
            model.addAttribute("perfis", Perfil.values());
            model.addAttribute("categorias", tipoSolicitacaoService.listarCategoriasDisponiveis());
            model.addAttribute("isEdicao", true);
            return "rh/formularios/formulario";
        }
    }

    @GetMapping("/{id}/campos")
    public String construtorCampos(@PathVariable("id") Integer id, Model model) {
        TipoSolicitacao tipo = tipoSolicitacaoService.buscarPorId(id);
        CampoFormularioForm campoForm = new CampoFormularioForm();
        campoForm.setOrdem(tipo.getCampos().size() + 1);

        model.addAttribute("formulario", tipo);
        model.addAttribute("campoForm", campoForm);
        model.addAttribute("tiposCampo", TipoCampo.values());
        return "rh/formularios/campos";
    }

    @PostMapping("/{id}/campos/adicionar")
    public String adicionarCampo(@PathVariable("id") Integer id,
                                 @Valid @ModelAttribute("campoForm") CampoFormularioForm campoForm,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            TipoSolicitacao tipo = tipoSolicitacaoService.buscarPorId(id);
            model.addAttribute("formulario", tipo);
            model.addAttribute("tiposCampo", TipoCampo.values());
            model.addAttribute("mensagemErro", "Preencha corretamente os dados do campo.");
            return "rh/formularios/campos";
        }

        try {
            tipoSolicitacaoService.adicionarCampo(id, campoForm);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Campo adicionado ao formulário com sucesso!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao adicionar campo: " + ex.getMessage());
        }

        return "redirect:/rh/gerenciar/formularios/" + id + "/campos";
    }

    @PostMapping("/{id}/campos/{campoId}/excluir")
    public String excluirCampo(@PathVariable("id") Integer id,
                               @PathVariable("campoId") Integer campoId,
                               RedirectAttributes redirectAttributes) {
        try {
            tipoSolicitacaoService.excluirCampo(campoId);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Campo removido com sucesso!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao remover campo: " + ex.getMessage());
        }
        return "redirect:/rh/gerenciar/formularios/" + id + "/campos";
    }

    @PostMapping("/{id}/toggle-status")
    public String alternarStatus(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        boolean novoStatus = tipoSolicitacaoService.alternarStatus(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Status do formulário alterado para " + (novoStatus ? "Ativo" : "Inativo") + ".");
        return "redirect:/rh/gerenciar/formularios";
    }

    @PostMapping("/{id}/excluir")
    public String excluirFormulario(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            tipoSolicitacaoService.excluir(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Formulário excluído com sucesso!");
        } catch (Exception ex) {
            // Se houver tickets vinculados, inativa amigavelmente
            tipoSolicitacaoService.alternarStatus(id);
            redirectAttributes.addFlashAttribute("mensagemAlerta", "O formulário possui chamados abertos e não pode ser excluído permanentemente; seu status foi alterado para Inativo.");
        }
        return "redirect:/rh/gerenciar/formularios";
    }
}

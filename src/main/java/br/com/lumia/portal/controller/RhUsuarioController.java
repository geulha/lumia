package br.com.lumia.portal.controller;

import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.Perfil;
import br.com.lumia.portal.dto.UsuarioForm;
import br.com.lumia.portal.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rh/gerenciar/usuarios")
public class RhUsuarioController {

    private final UsuarioService usuarioService;

    public RhUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "rh/usuarios/lista";
    }

    @GetMapping("/novo")
    public String novoUsuarioForm(Model model) {
        UsuarioForm form = new UsuarioForm();
        model.addAttribute("usuarioForm", form);
        model.addAttribute("gestores", usuarioService.listarPossiveisGestores(null));
        model.addAttribute("perfis", Perfil.values());
        model.addAttribute("isEdicao", false);
        return "rh/usuarios/formulario";
    }

    @PostMapping("/novo")
    public String criarUsuario(@Valid @ModelAttribute("usuarioForm") UsuarioForm form,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("gestores", usuarioService.listarPossiveisGestores(null));
            model.addAttribute("perfis", Perfil.values());
            model.addAttribute("isEdicao", false);
            return "rh/usuarios/formulario";
        }

        try {
            String senhaTemporaria = usuarioService.criar(form);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuário cadastrado com sucesso!");
            redirectAttributes.addFlashAttribute("senhaTemporaria", senhaTemporaria);
            redirectAttributes.addFlashAttribute("emailCadastrado", form.getEmail());
            return "redirect:/rh/gerenciar/usuarios";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("mensagemErro", ex.getMessage());
            model.addAttribute("gestores", usuarioService.listarPossiveisGestores(null));
            model.addAttribute("perfis", Perfil.values());
            model.addAttribute("isEdicao", false);
            return "rh/usuarios/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editarUsuarioForm(@PathVariable("id") Integer id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);
        UsuarioForm form = new UsuarioForm();
        form.setId(usuario.getId());
        form.setNome(usuario.getNome());
        form.setEmail(usuario.getEmail());
        form.setCpf(usuario.getCpf());
        form.setPerfil(usuario.getPerfil());
        form.setPosto(usuario.getPosto());
        form.setGestorId(usuario.getGestor() != null ? usuario.getGestor().getId() : null);
        form.setAtivo(usuario.getAtivo());
        form.setBloqueado(usuario.getBloqueado());

        model.addAttribute("usuarioForm", form);
        model.addAttribute("gestores", usuarioService.listarPossiveisGestores(id));
        model.addAttribute("perfis", Perfil.values());
        model.addAttribute("isEdicao", true);
        model.addAttribute("usuarioNome", usuario.getNome());
        return "rh/usuarios/formulario";
    }

    @PostMapping("/{id}/editar")
    public String atualizarUsuario(@PathVariable("id") Integer id,
                                   @Valid @ModelAttribute("usuarioForm") UsuarioForm form,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("gestores", usuarioService.listarPossiveisGestores(id));
            model.addAttribute("perfis", Perfil.values());
            model.addAttribute("isEdicao", true);
            return "rh/usuarios/formulario";
        }

        try {
            usuarioService.atualizar(id, form);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Dados do usuário atualizados com sucesso!");
            return "redirect:/rh/gerenciar/usuarios";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("mensagemErro", ex.getMessage());
            model.addAttribute("gestores", usuarioService.listarPossiveisGestores(id));
            model.addAttribute("perfis", Perfil.values());
            model.addAttribute("isEdicao", true);
            return "rh/usuarios/formulario";
        }
    }

    @PostMapping("/{id}/toggle-status")
    public String alternarStatus(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        boolean novoStatus = usuarioService.alternarStatusAtivo(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Status do usuário alterado para " + (novoStatus ? "Ativo" : "Inativo") + ".");
        return "redirect:/rh/gerenciar/usuarios";
    }

    @PostMapping("/{id}/toggle-bloqueio")
    public String alternarBloqueio(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        boolean novoBloqueio = usuarioService.alternarStatusBloqueio(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Acesso do usuário " + (novoBloqueio ? "Bloqueado" : "Desbloqueado") + ".");
        return "redirect:/rh/gerenciar/usuarios";
    }

    @PostMapping("/{id}/reset-senha")
    public String resetarSenha(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.buscarPorId(id);
        String novaSenha = usuarioService.gerarNovaSenhaTemporaria(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Nova senha temporária gerada com sucesso!");
        redirectAttributes.addFlashAttribute("senhaTemporaria", novaSenha);
        redirectAttributes.addFlashAttribute("emailCadastrado", usuario.getEmail());
        return "redirect:/rh/gerenciar/usuarios";
    }
}

package br.com.lumia.portal;

import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.Perfil;
import br.com.lumia.portal.dto.UsuarioForm;
import br.com.lumia.portal.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UsuarioManagementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testCriarUsuarioGeraSenhaTemporariaValida() {
        UsuarioForm form = new UsuarioForm();
        form.setNome("Novo Funcionario Teste");
        form.setEmail("novo.teste@lumia.com.br");
        form.setCpf("12345678901");
        form.setPerfil(Perfil.COLABORADOR);
        form.setPosto("Assistente Administrativo");
        form.setGestorId(2); // Helen Sturka

        String senhaTemporaria = usuarioService.criar(form);

        assertNotNull(senhaTemporaria);
        assertEquals(10, senhaTemporaria.length());

        Usuario criado = usuarioService.listarTodos().stream()
                .filter(u -> u.getEmail().equals("novo.teste@lumia.com.br"))
                .findFirst()
                .orElseThrow();

        assertEquals("Novo Funcionario Teste", criado.getNome());
        assertEquals("Assistente Administrativo", criado.getPosto());
        assertEquals(2, criado.getGestor().getId());
        assertTrue(passwordEncoder.matches(senhaTemporaria, criado.getSenhaHash()));
    }

    @Test
    void testAlternarStatusEBloqueio() {
        Usuario usuario = usuarioService.buscarPorId(3); // Carol Sturka
        assertTrue(usuario.getAtivo());
        assertFalse(usuario.getBloqueado());

        // Inativar
        usuarioService.alternarStatusAtivo(3);
        assertFalse(usuarioService.buscarPorId(3).getAtivo());

        // Reativar
        usuarioService.alternarStatusAtivo(3);
        assertTrue(usuarioService.buscarPorId(3).getAtivo());

        // Bloquear
        usuarioService.alternarStatusBloqueio(3);
        assertTrue(usuarioService.buscarPorId(3).getBloqueado());
    }

    @Test
    @WithMockUser(username = "zosia.hivemind@lumia.com.br", roles = {"RH"})
    void rhCanListAndCreateUsersViaController() throws Exception {
        mockMvc.perform(get("/rh/gerenciar/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("rh/usuarios/lista"))
                .andExpect(model().attributeExists("usuarios"));

        mockMvc.perform(post("/rh/gerenciar/usuarios/novo")
                        .with(csrf())
                        .param("nome", "Lucas Silva")
                        .param("email", "lucas.silva@lumia.com.br")
                        .param("perfil", "COLABORADOR")
                        .param("posto", "Estagiário")
                        .param("gestorId", "2")
                        .param("ativo", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rh/gerenciar/usuarios"))
                .andExpect(flash().attributeExists("senhaTemporaria"));
    }

    @Test
    @WithMockUser(username = "carol.sturka@lumia.com.br", roles = {"COLABORADOR"})
    void colaboradorCannotAccessGerenciarUsuarios() throws Exception {
        mockMvc.perform(get("/rh/gerenciar/usuarios"))
                .andExpect(status().isForbidden());
    }
}

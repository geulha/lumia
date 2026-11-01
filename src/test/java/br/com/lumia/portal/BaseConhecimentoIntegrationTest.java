package br.com.lumia.portal;

import br.com.lumia.portal.domain.entity.Artigo;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.dto.ArtigoForm;
import br.com.lumia.portal.service.ArtigoService;
import br.com.lumia.portal.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
public class BaseConhecimentoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtigoService artigoService;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    @WithMockUser(username = "carol.sturka@lumia.com.br", roles = {"COLABORADOR"})
    void colaboradorCanListAndReadArticles() throws Exception {
        mockMvc.perform(get("/artigos"))
                .andExpect(status().isOk())
                .andExpect(view().name("artigos/lista"))
                .andExpect(model().attributeExists("artigos"));

        mockMvc.perform(get("/artigos?termo=Férias"))
                .andExpect(status().isOk())
                .andExpect(view().name("artigos/lista"));

        mockMvc.perform(get("/artigos/183"))
                .andExpect(status().isOk())
                .andExpect(view().name("artigos/detalhe"))
                .andExpect(model().attributeExists("artigo"));
    }

    @Test
    void testCriarAtualizarETogglePublicacao() {
        Usuario zosia = usuarioService.buscarPorId(1); // Zosia Hivemind

        ArtigoForm form = new ArtigoForm();
        form.setTitulo("Política de Trabalho Remoto / Home Office");
        form.setConteudo("Orientações sobre ajuda de custo de ergonomia e jornada flexível.");
        form.setCategoria("Benefícios");
        form.setPublicado(true);

        Artigo criado = artigoService.criar(form, zosia);
        assertNotNull(criado.getId());
        assertEquals("ART" + String.format("%04d", criado.getId()), criado.getCodigoFormatado());
        assertTrue(criado.getPublicado());

        // Despublicar
        artigoService.alternarPublicacao(criado.getId());
        Artigo despublicado = artigoService.buscarPorId(criado.getId());
        assertFalse(despublicado.getPublicado());

        // Atualizar
        form.setTitulo("Política de Trabalho Remoto Atualizada");
        artigoService.atualizar(criado.getId(), form);
        Artigo atualizado = artigoService.buscarPorId(criado.getId());
        assertEquals("Política de Trabalho Remoto Atualizada", atualizado.getTitulo());
    }

    @Test
    @WithMockUser(username = "zosia.hivemind@lumia.com.br", roles = {"RH"})
    void rhCanManageArticles() throws Exception {
        mockMvc.perform(get("/rh/gerenciar/base-conhecimento"))
                .andExpect(status().isOk())
                .andExpect(view().name("rh/artigos/lista"))
                .andExpect(model().attributeExists("artigos"));
    }

    @Test
    @WithMockUser(username = "carol.sturka@lumia.com.br", roles = {"COLABORADOR"})
    void colaboradorCannotAccessGerenciarBaseConhecimento() throws Exception {
        mockMvc.perform(get("/rh/gerenciar/base-conhecimento"))
                .andExpect(status().isForbidden());
    }
}

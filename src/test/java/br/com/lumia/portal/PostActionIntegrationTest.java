package br.com.lumia.portal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PostActionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "zosia.hivemind@lumia.com.br", roles = {"RH"})
    void testRhComentar() throws Exception {
        mockMvc.perform(post("/rh/tickets/81637/comentar")
                .with(csrf())
                .param("conteudo", "Mensagem de teste devolutiva"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "zosia.hivemind@lumia.com.br", roles = {"RH"})
    void testRhAtualizarStatus() throws Exception {
        mockMvc.perform(post("/rh/tickets/81637/status")
                .with(csrf())
                .param("status", "EM_ANDAMENTO")
                .param("justificativa", "Em analise"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "carol.sturka@lumia.com.br", roles = {"COLABORADOR"})
    void testColaboradorComentar() throws Exception {
        mockMvc.perform(post("/colaborador/solicitacoes/83619/comentar")
                .with(csrf())
                .param("conteudo", "Mensagem de teste colaborador"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "helen.sturka@lumia.com.br", roles = {"COLABORADOR"})
    void testGestorDecidirAprovacao() throws Exception {
        mockMvc.perform(post("/aprovacoes/1/decidir")
                .with(csrf())
                .param("decisao", "APROVADO")
                .param("comentario", "Aprovado em teste"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }
}

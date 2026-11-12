package br.com.lumia.portal;

import br.com.lumia.portal.domain.entity.Ticket;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.StatusTicket;
import br.com.lumia.portal.service.TicketService;
import br.com.lumia.portal.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class TicketColaboradorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    @WithMockUser(username = "carol.sturka@lumia.com.br", roles = {"COLABORADOR"})
    void colaboradorCanAccessHomeAndListSolicitacoes() throws Exception {
        mockMvc.perform(get("/colaborador"))
                .andExpect(status().isOk())
                .andExpect(view().name("colaborador/home"))
                .andExpect(model().attributeExists("minhasSolicitacoes"))
                .andExpect(model().attributeExists("artigosRecentes"));

        mockMvc.perform(get("/colaborador/solicitacoes"))
                .andExpect(status().isOk())
                .andExpect(view().name("colaborador/solicitacoes/lista"))
                .andExpect(model().attributeExists("solicitacoes"));

        mockMvc.perform(get("/colaborador/solicitacoes").param("termo", "83619"))
                .andExpect(status().isOk())
                .andExpect(view().name("colaborador/solicitacoes/lista"))
                .andExpect(model().attributeExists("solicitacoes"))
                .andExpect(model().attribute("termo", "83619"));
    }

    @Test
    @WithMockUser(username = "carol.sturka@lumia.com.br", roles = {"COLABORADOR"})
    void colaboradorCanViewOwnTicket() throws Exception {
        // REQ0083619 pertence a Carol Sturka
        mockMvc.perform(get("/colaborador/solicitacoes/83619"))
                .andExpect(status().isOk())
                .andExpect(view().name("colaborador/solicitacoes/detalhes"))
                .andExpect(model().attributeExists("ticket"));
    }

    @Test
    @WithMockUser(username = "carol.sturka@lumia.com.br", roles = {"COLABORADOR"})
    void colaboradorCannotViewOtherColaboradorTicket() throws Exception {
        // Ticket 7362 pertence a Giulia Lain, não à Carol
        mockMvc.perform(get("/colaborador/solicitacoes/7362"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAbrirTicketDinamicoComSucesso() {
        Usuario carol = usuarioService.buscarPorId(3); // Carol Sturka

        Map<String, String> respostasTexto = new HashMap<>();
        respostasTexto.put("campo_5", "Julho"); // Mês de Referência
        respostasTexto.put("campo_6", "Divergência de cálculo no vale transporte."); // Descrição

        Ticket ticket = ticketService.abrirTicket(carol, 2, respostasTexto, null); // Formulário 2: Correção de Folha
        assertNotNull(ticket.getId());
        assertEquals(StatusTicket.ABERTO, ticket.getStatus());
        assertEquals("Julho", ticket.getRespostas().stream()
                .filter(r -> r.getCampo().getId().equals(5))
                .findFirst().orElseThrow().getValorTexto());
    }
}

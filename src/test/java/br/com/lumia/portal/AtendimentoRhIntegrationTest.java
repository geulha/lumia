package br.com.lumia.portal;

import br.com.lumia.portal.domain.entity.Ticket;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.StatusTicket;
import br.com.lumia.portal.domain.enums.TipoInteracao;
import br.com.lumia.portal.service.TicketService;
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
public class AtendimentoRhIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    @WithMockUser(username = "zosia.hivemind@lumia.com.br", roles = {"RH"})
    void rhCanAccessDashboardWithIndicators() throws Exception {
        mockMvc.perform(get("/rh"))
                .andExpect(status().isOk())
                .andExpect(view().name("rh/dashboard"))
                .andExpect(model().attributeExists("contagemAbertos"))
                .andExpect(model().attributeExists("contagemAndamento"))
                .andExpect(model().attributeExists("contagemPendentes"))
                .andExpect(model().attributeExists("contagemEncerrados"))
                .andExpect(model().attributeExists("meuTrabalho"));
    }

    @Test
    @WithMockUser(username = "zosia.hivemind@lumia.com.br", roles = {"RH"})
    void rhCanAccessOpenTicketsQueue() throws Exception {
        mockMvc.perform(get("/rh/tickets/abertos"))
                .andExpect(status().isOk())
                .andExpect(view().name("rh/tickets/abertos"))
                .andExpect(model().attributeExists("tickets"));
    }

    @Test
    void testAtribuirAnalistaEAlterarStatus() {
        Usuario zosia = usuarioService.buscarPorId(1); // Zosia Hivemind
        Ticket ticket = ticketService.buscarPorId(81637); // REQ0081637 (ABERTO)

        // Atribuir ao analista
        ticketService.atribuirAnalista(81637, zosia);
        Ticket atualizado = ticketService.buscarPorId(81637);
        assertEquals(zosia.getId(), atualizado.getAnalista().getId());
        assertEquals(StatusTicket.EM_ANDAMENTO, atualizado.getStatus());

        // Alterar status para CONCLUIDO com justificativa
        ticketService.alterarStatus(81637, zosia, StatusTicket.CONCLUIDO, "Folha de pagamento corrigida na competência vigente.");
        Ticket concluido = ticketService.buscarPorId(81637);
        assertEquals(StatusTicket.CONCLUIDO, concluido.getStatus());

        // Verificar interações registradas no histórico
        assertTrue(concluido.getInteracoes().stream()
                .anyMatch(i -> i.getTipo() == TipoInteracao.MUDANCA_STATUS && i.getStatusPara() == StatusTicket.CONCLUIDO));
    }

    @Test
    @WithMockUser(username = "carol.sturka@lumia.com.br", roles = {"COLABORADOR"})
    void colaboradorCannotAccessRhTicketsAbertos() throws Exception {
        mockMvc.perform(get("/rh/tickets/abertos"))
                .andExpect(status().isForbidden());
    }
}

package br.com.lumia.portal;

import br.com.lumia.portal.domain.entity.Aprovacao;
import br.com.lumia.portal.domain.entity.Ticket;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.DecisaoAprovacao;
import br.com.lumia.portal.domain.enums.StatusTicket;
import br.com.lumia.portal.service.AprovacaoService;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AprovacaoGestorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AprovacaoService aprovacaoService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private br.com.lumia.portal.service.NotificacaoService notificacaoService;

    @Test
    void testListarPendenciasDeGestor() {
        Usuario helen = usuarioService.buscarPorId(2); // Helen Sturka
        List<Aprovacao> pendentes = aprovacaoService.listarPendentes(helen);
        assertFalse(pendentes.isEmpty(), "Helen deve ter aprovações pendentes de sua equipe");

        // Verifica se Helen possui notificação de aprovação pendente
        List<br.com.lumia.portal.domain.entity.Notificacao> notifs = notificacaoService.listarTodas(helen);
        assertTrue(notifs.stream().anyMatch(n -> n.getTipo() == br.com.lumia.portal.domain.enums.TipoNotificacao.APROVACAO_PENDENTE),
                "O gestor deve receber notificação de aprovação pendente");
    }

    @Test
    void testAprovarSolicitacaoEncaminhaAoRhComoAberto() {
        Usuario helen = usuarioService.buscarPorId(2);
        List<Aprovacao> pendentes = aprovacaoService.listarPendentes(helen);
        Aprovacao ap = pendentes.get(0);
        Integer ticketId = ap.getTicket().getId();

        // Aprovar
        aprovacaoService.decidir(ap.getId(), helen, DecisaoAprovacao.APROVADO, "Aprovado conforme combinado.");

        Aprovacao recarregada = aprovacaoService.buscarPorId(ap.getId());
        assertEquals(DecisaoAprovacao.APROVADO, recarregada.getDecisao());

        Ticket ticket = ticketService.buscarPorId(ticketId);
        assertEquals(StatusTicket.ABERTO, ticket.getStatus(), "Ticket aprovado pelo gestor deve transitar para ABERTO na fila do RH");
    }

    @Test
    void testRejeitarSolicitacaoEncerraComoReprovado() {
        Usuario helen = usuarioService.buscarPorId(2);
        List<Aprovacao> pendentes = aprovacaoService.listarPendentes(helen);
        Aprovacao ap = pendentes.get(0);
        Integer ticketId = ap.getTicket().getId();

        // Rejeitar
        aprovacaoService.decidir(ap.getId(), helen, DecisaoAprovacao.REJEITADO, "Reprovado por conflito de escala na equipe.");

        Aprovacao recarregada = aprovacaoService.buscarPorId(ap.getId());
        assertEquals(DecisaoAprovacao.REJEITADO, recarregada.getDecisao());

        Ticket ticket = ticketService.buscarPorId(ticketId);
        assertEquals(StatusTicket.REPROVADO, ticket.getStatus(), "Ticket rejeitado pelo gestor deve ser encerrado com status REPROVADO");
    }

    @Test
    @WithMockUser(username = "helen.sturka@lumia.com.br", roles = {"COLABORADOR"})
    void gestorCanAccessMinhasAprovacoes() throws Exception {
        mockMvc.perform(get("/aprovacoes"))
                .andExpect(status().isOk())
                .andExpect(view().name("aprovacoes/lista"))
                .andExpect(model().attributeExists("pendentes"));
    }
}

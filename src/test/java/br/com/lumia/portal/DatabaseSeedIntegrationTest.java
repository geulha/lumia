package br.com.lumia.portal;

import br.com.lumia.portal.domain.entity.Ticket;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.Perfil;
import br.com.lumia.portal.domain.enums.StatusTicket;
import br.com.lumia.portal.repository.ArtigoRepository;
import br.com.lumia.portal.repository.TicketRepository;
import br.com.lumia.portal.repository.TipoSolicitacaoRepository;
import br.com.lumia.portal.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class DatabaseSeedIntegrationTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoSolicitacaoRepository tipoSolicitacaoRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ArtigoRepository artigoRepository;

    @Test
    void testSeedDataLoadedProperly() {
        // Verifica RH
        Optional<Usuario> zosia = usuarioRepository.findByEmailIgnoreCase("zosia.hivemind@lumia.com.br");
        assertTrue(zosia.isPresent(), "Usuária Zosia Hivemind deve existir");
        assertEquals(Perfil.RH, zosia.get().getPerfil());

        // Verifica Gestora
        Optional<Usuario> helen = usuarioRepository.findByEmailIgnoreCase("helen.sturka@lumia.com.br");
        assertTrue(helen.isPresent());
        assertEquals("Gerente de Operações", helen.get().getPosto());

        // Verifica Colaboradora vinculada à gestora
        Optional<Usuario> carol = usuarioRepository.findByEmailIgnoreCase("carol.sturka@lumia.com.br");
        assertTrue(carol.isPresent());
        assertNotNull(carol.get().getGestor());
        assertEquals(helen.get().getId(), carol.get().getGestor().getId());

        // Verifica formulários dinâmicos
        assertEquals(6, tipoSolicitacaoRepository.count());

        // Verifica artigos
        assertEquals(4, artigoRepository.count());

        // Verifica tickets e seus status
        List<Ticket> tickets = ticketRepository.findAll();
        assertFalse(tickets.isEmpty());

        // Verifica existência de todos os status requeridos
        assertTrue(tickets.stream().anyMatch(t -> t.getStatus() == StatusTicket.ABERTO));
        assertTrue(tickets.stream().anyMatch(t -> t.getStatus() == StatusTicket.EM_ANDAMENTO));
        assertTrue(tickets.stream().anyMatch(t -> t.getStatus() == StatusTicket.PENDENTE));
        assertTrue(tickets.stream().anyMatch(t -> t.getStatus() == StatusTicket.CONCLUIDO));
        assertTrue(tickets.stream().anyMatch(t -> t.getStatus() == StatusTicket.REPROVADO));

        // Verifica código formatado
        Ticket ticket83619 = ticketRepository.findById(83619).orElseThrow();
        assertEquals("REQ0083619", ticket83619.getCodigoFormatado());
    }
}

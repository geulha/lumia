package br.com.lumia.portal;

import br.com.lumia.portal.domain.entity.Artigo;
import br.com.lumia.portal.domain.entity.TipoSolicitacao;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.Perfil;
import br.com.lumia.portal.service.ArtigoService;
import br.com.lumia.portal.service.TipoSolicitacaoService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class NovosAjustesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TipoSolicitacaoService tipoSolicitacaoService;

    @Autowired
    private ArtigoService artigoService;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    void testBuscaAccentInsensitive() {
        Usuario carol = usuarioService.buscarPorId(3); // Carol Sturka (COLABORADOR)

        // Busca de formulários sem acento deve encontrar com acento
        List<TipoSolicitacao> formsSemAcento = tipoSolicitacaoService.buscarPorTermo(carol, "ferias");
        assertFalse(formsSemAcento.isEmpty(), "Deveria encontrar formulário pesquisando 'ferias' sem acento");
        assertTrue(formsSemAcento.stream().anyMatch(f -> f.getNome().contains("Férias")));

        // Busca de formulários com acento
        List<TipoSolicitacao> formsComAcento = tipoSolicitacaoService.buscarPorTermo(carol, "férias");
        assertFalse(formsComAcento.isEmpty(), "Deveria encontrar formulário pesquisando 'férias' com acento");
        assertEquals(formsSemAcento.size(), formsComAcento.size());

        // Busca de artigos sem acento
        List<Artigo> artsSemAcento = artigoService.buscarPublicadosPorTermo(carol, "ferias");
        assertFalse(artsSemAcento.isEmpty(), "Deveria encontrar artigo pesquisando 'ferias' sem acento");

        // Busca de artigos com acento
        List<Artigo> artsComAcento = artigoService.buscarPublicadosPorTermo(carol, "férias");
        assertFalse(artsComAcento.isEmpty(), "Deveria encontrar artigo pesquisando 'férias' com acento");
        assertEquals(artsSemAcento.size(), artsComAcento.size());
    }

    @Test
    @WithMockUser(username = "zosia.hivemind@lumia.com.br", roles = {"RH"})
    void rhCanAccessFormulariosToOpenRequests() throws Exception {
        // RH pode acessar a lista de formulários e a tela de abertura de solicitação
        mockMvc.perform(get("/colaborador/formularios"))
                .andExpect(status().isOk())
                .andExpect(view().name("colaborador/formularios"))
                .andExpect(model().attributeExists("formularios"));

        mockMvc.perform(get("/colaborador/solicitacoes/nova/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("colaborador/solicitacoes/abrir"));
    }

    @Test
    @WithMockUser(username = "zosia.hivemind@lumia.com.br", roles = {"RH"})
    void rhCanAccessPendentesAndEncerrados() throws Exception {
        mockMvc.perform(get("/rh/tickets/pendentes"))
                .andExpect(status().isOk())
                .andExpect(view().name("rh/tickets/pendentes"))
                .andExpect(model().attributeExists("tickets"));

        mockMvc.perform(get("/rh/tickets/encerrados"))
                .andExpect(status().isOk())
                .andExpect(view().name("rh/tickets/encerrados"))
                .andExpect(model().attributeExists("tickets"));
    }

    @Test
    @WithMockUser(username = "carol.sturka@lumia.com.br", roles = {"COLABORADOR"})
    void globalSearchWorksWithOrWithoutAccents() throws Exception {
        mockMvc.perform(get("/busca").param("termo", "ferias"))
                .andExpect(status().isOk())
                .andExpect(view().name("busca"))
                .andExpect(model().attributeExists("formularios"))
                .andExpect(model().attributeExists("artigos"));

        mockMvc.perform(get("/busca").param("termo", "férias"))
                .andExpect(status().isOk())
                .andExpect(view().name("busca"))
                .andExpect(model().attributeExists("formularios"))
                .andExpect(model().attributeExists("artigos"));
    }

    @Test
    @WithMockUser(username = "carol.sturka@lumia.com.br", roles = {"COLABORADOR"})
    void testNotificacoesFluxo() throws Exception {
        mockMvc.perform(get("/notificacoes"))
                .andExpect(status().isOk())
                .andExpect(view().name("notificacoes/lista"))
                .andExpect(model().attributeExists("notificacoes"));
    }
}

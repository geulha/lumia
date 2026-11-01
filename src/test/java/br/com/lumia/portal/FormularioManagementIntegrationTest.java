package br.com.lumia.portal;

import br.com.lumia.portal.domain.entity.CampoFormulario;
import br.com.lumia.portal.domain.entity.TipoSolicitacao;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.TipoCampo;
import br.com.lumia.portal.dto.CampoFormularioForm;
import br.com.lumia.portal.dto.TipoSolicitacaoForm;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class FormularioManagementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TipoSolicitacaoService tipoSolicitacaoService;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    void testCriarFormularioEAdicionarCamposDinamicos() {
        Usuario rh = usuarioService.buscarPorId(1); // Zosia Hivemind

        TipoSolicitacaoForm form = new TipoSolicitacaoForm();
        form.setNome("Auxílio Creche");
        form.setDescricao("Solicitação de reembolso ou concessão de auxílio creche.");
        form.setCategoria("Benefícios");
        form.setRequerAprovacao(false);
        form.setAtivo(true);

        TipoSolicitacao criado = tipoSolicitacaoService.criar(form, rh);
        assertNotNull(criado.getId());
        assertEquals("Auxílio Creche", criado.getNome());
        assertEquals("Benefícios", criado.getCategoria());

        // Adicionar campo TEXTO
        CampoFormularioForm campo1 = new CampoFormularioForm();
        campo1.setRotulo("Nome do Filho(a)");
        campo1.setTipoCampo(TipoCampo.TEXTO);
        campo1.setObrigatorio(true);
        campo1.setOrdem(1);
        CampoFormulario c1 = tipoSolicitacaoService.adicionarCampo(criado.getId(), campo1);
        assertNotNull(c1.getId());

        // Adicionar campo SELECAO
        CampoFormularioForm campo2 = new CampoFormularioForm();
        campo2.setRotulo("Turno da Creche");
        campo2.setTipoCampo(TipoCampo.SELECAO);
        campo2.setOpcoes("Integral, Matutino, Vespertino");
        campo2.setObrigatorio(true);
        campo2.setOrdem(2);
        CampoFormulario c2 = tipoSolicitacaoService.adicionarCampo(criado.getId(), campo2);
        assertTrue(c2.getListaOpcoes().contains("Integral"));

        // Adicionar campo ANEXO
        CampoFormularioForm campo3 = new CampoFormularioForm();
        campo3.setRotulo("Certidão de Nascimento");
        campo3.setTipoCampo(TipoCampo.ANEXO);
        campo3.setObrigatorio(true);
        campo3.setOrdem(3);
        CampoFormulario c3 = tipoSolicitacaoService.adicionarCampo(criado.getId(), campo3);
        assertNotNull(c3.getId());

        TipoSolicitacao recarregado = tipoSolicitacaoService.buscarPorId(criado.getId());
        assertEquals(3, recarregado.getCampos().size());

        // Testar exclusão de campo
        tipoSolicitacaoService.excluirCampo(c1.getId());
        TipoSolicitacao aposExclusao = tipoSolicitacaoService.buscarPorId(criado.getId());
        assertEquals(2, aposExclusao.getCampos().size());
    }

    @Test
    @WithMockUser(username = "zosia.hivemind@lumia.com.br", roles = {"RH"})
    void rhCanAccessFormulariosManagement() throws Exception {
        mockMvc.perform(get("/rh/gerenciar/formularios"))
                .andExpect(status().isOk())
                .andExpect(view().name("rh/formularios/lista"))
                .andExpect(model().attributeExists("formularios"));

        mockMvc.perform(get("/rh/gerenciar/formularios/1/campos"))
                .andExpect(status().isOk())
                .andExpect(view().name("rh/formularios/campos"))
                .andExpect(model().attributeExists("formulario"));
    }

    @Test
    @WithMockUser(username = "carol.sturka@lumia.com.br", roles = {"COLABORADOR"})
    void colaboradorCannotAccessGerenciarFormularios() throws Exception {
        mockMvc.perform(get("/rh/gerenciar/formularios"))
                .andExpect(status().isForbidden());
    }
}

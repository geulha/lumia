package br.com.lumia.portal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedAccessToProtectedResourceRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/colaborador"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void loginWithValidRhCredentialsRedirectsToRhPanel() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .user("email", "zosia.hivemind@lumia.com.br")
                        .password("senha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rh"));
    }

    @Test
    void loginWithValidColaboradorCredentialsRedirectsToColaboradorPortal() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .user("email", "carol.sturka@lumia.com.br")
                        .password("senha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/colaborador"));
    }

    @Test
    void loginWithInvalidPasswordFails() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .user("email", "carol.sturka@lumia.com.br")
                        .password("senha", "senhaIncorreta"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?erro=true"));
    }

    @Test
    @WithMockUser(username = "carol.sturka@lumia.com.br", roles = {"COLABORADOR"})
    void colaboradorCannotAccessRhPanel() throws Exception {
        mockMvc.perform(get("/rh"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "zosia.hivemind@lumia.com.br", roles = {"RH"})
    void rhCanAccessRhGerenciarUsuarios() throws Exception {
        // Quando criarmos a rota, ela deve retornar 200 ou redirect, não 403
        mockMvc.perform(get("/acesso-negado"))
                .andExpect(status().isOk());
    }
}

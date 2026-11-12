package br.com.lumia.portal.service;

import br.com.lumia.portal.domain.entity.Aprovacao;
import br.com.lumia.portal.domain.entity.Artigo;
import br.com.lumia.portal.domain.entity.Notificacao;
import br.com.lumia.portal.domain.entity.Ticket;
import br.com.lumia.portal.domain.entity.TipoSolicitacao;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.Perfil;
import br.com.lumia.portal.domain.enums.TipoNotificacao;
import br.com.lumia.portal.repository.NotificacaoRepository;
import br.com.lumia.portal.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository,
                              UsuarioRepository usuarioRepository) {
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Notificacao criar(Usuario destinatario, String titulo, String mensagem, String link, TipoNotificacao tipo) {
        if (destinatario == null) return null;
        Notificacao notif = new Notificacao(destinatario, titulo, mensagem, link, tipo);
        return notificacaoRepository.save(notif);
    }

    /**
     * Gatilho de Aprovação Pendente:
     * Notifica o gestor/aprovador quando uma solicitação é aberta e fica pendente de sua decisão.
     */
    @Transactional
    public void notificarAprovacaoPendente(Ticket ticket, Aprovacao aprovacao) {
        if (ticket == null || aprovacao == null || aprovacao.getAprovador() == null) return;

        Usuario aprovador = aprovacao.getAprovador();
        String titulo = "Nova aprovação pendente: " + ticket.getCodigoFormatado();
        String solicitanteNome = ticket.getColaborador() != null ? ticket.getColaborador().getNome() : "Colaborador";
        String servicoNome = ticket.getTipoSolicitacao() != null ? ticket.getTipoSolicitacao().getNome() : "Solicitação";
        String msg = solicitanteNome + " abriu o chamado '" + servicoNome + "' (" + ticket.getCodigoFormatado() + ") que aguarda sua avaliação e aprovação.";
        String link = "/aprovacoes/" + aprovacao.getId();

        criar(aprovador, titulo, msg, link, TipoNotificacao.APROVACAO_PENDENTE);
    }

    /**
     * Gatilho 1 & 4:
     * - Quando uma solicitação aberta pela pessoa é atualizada -> notifica o solicitante (colaborador ou rh).
     * - Quando um ticket pelo qual o analista está responsável é atualizado -> notifica o analista responsável.
     */
    @Transactional
    public void notificarAtualizacaoTicket(Ticket ticket, Usuario autorDaAcao, String resumoAcao) {
        if (ticket == null) return;

        // 1. Notifica o Solicitante se não foi ele quem fez a ação
        Usuario solicitante = ticket.getColaborador();
        if (solicitante != null && (autorDaAcao == null || !solicitante.getId().equals(autorDaAcao.getId()))) {
            String link = "/colaborador/solicitacoes/" + ticket.getId();
            String titulo = "Atualização no chamado " + ticket.getCodigoFormatado();
            String msg = resumoAcao != null && !resumoAcao.isBlank()
                    ? resumoAcao
                    : "Houve uma nova movimentação no seu chamado '" + ticket.getTipoSolicitacao().getNome() + "'.";

            criar(solicitante, titulo, msg, link, TipoNotificacao.TICKET_ATUALIZADO);
        }

        // 4. Notifica o Analista Responsável se existir e não foi ele quem fez a ação
        Usuario analista = ticket.getAnalista();
        if (analista != null && (autorDaAcao == null || !analista.getId().equals(autorDaAcao.getId()))) {
            String link = "/rh/tickets/" + ticket.getId();
            String titulo = "Atualização no chamado " + ticket.getCodigoFormatado();
            String msg = resumoAcao != null && !resumoAcao.isBlank()
                    ? resumoAcao
                    : "O chamado sob sua responsabilidade recebeu uma nova atualização de " + (autorDaAcao != null ? autorDaAcao.getNome() : "um usuário") + ".";

            criar(analista, titulo, msg, link, TipoNotificacao.TICKET_ATUALIZADO);
        }
    }

    /**
     * Gatilho 2:
     * Quando um novo artigo é publicado -> notifica todos que possuem visualização do artigo.
     */
    @Transactional
    public void notificarNovoArtigo(Artigo artigo) {
        if (artigo == null || !Boolean.TRUE.equals(artigo.getPublicado())) return;

        List<Usuario> usuariosAtivos = usuarioRepository.findByAtivoTrueOrderByNomeAsc();
        for (Usuario u : usuariosAtivos) {
            if (podeVisualizarArtigo(u, artigo)) {
                String titulo = "Novo artigo na Base de Conhecimento";
                String msg = "Publicado: '" + artigo.getTitulo() + "' na categoria " + artigo.getCategoria() + ".";
                String link = "/artigos/" + artigo.getId();
                criar(u, titulo, msg, link, TipoNotificacao.NOVO_ARTIGO);
            }
        }
    }

    /**
     * Gatilho 3:
     * Quando um novo formulário é criado/disponibilizado -> notifica todos que possuem visualização do serviço.
     */
    @Transactional
    public void notificarNovoFormulario(TipoSolicitacao formulario) {
        if (formulario == null || !Boolean.TRUE.equals(formulario.getAtivo())) return;

        List<Usuario> usuariosAtivos = usuarioRepository.findByAtivoTrueOrderByNomeAsc();
        for (Usuario u : usuariosAtivos) {
            if (podeVisualizarFormulario(u, formulario)) {
                String titulo = "Novo serviço disponível";
                String msg = "Novo formulário de solicitação disponível: '" + formulario.getNome() + "' (" + formulario.getCategoria() + ").";
                String link = "/colaborador/solicitacoes/nova/" + formulario.getId();
                criar(u, titulo, msg, link, TipoNotificacao.NOVO_FORMULARIO);
            }
        }
    }

    private boolean podeVisualizarArtigo(Usuario usuario, Artigo artigo) {
        if (usuario.isRh()) return true;
        Perfil perfil = artigo.getVisivelParaPerfil();
        if (perfil != null && perfil != usuario.getPerfil()) return false;
        String posto = artigo.getVisivelParaPosto();
        if (posto != null && !posto.isBlank()) {
            return usuario.getPosto() != null && usuario.getPosto().equalsIgnoreCase(posto.trim());
        }
        return true;
    }

    private boolean podeVisualizarFormulario(Usuario usuario, TipoSolicitacao form) {
        if (usuario.isRh()) return true;
        Perfil perfil = form.getVisivelParaPerfil();
        if (perfil != null && perfil != usuario.getPerfil()) return false;
        String posto = form.getVisivelParaPosto();
        if (posto != null && !posto.isBlank()) {
            return usuario.getPosto() != null && usuario.getPosto().equalsIgnoreCase(posto.trim());
        }
        return true;
    }

    public List<Notificacao> listarRecentes(Usuario usuario) {
        if (usuario == null) return List.of();
        return notificacaoRepository.findTop8ByUsuarioIdOrderByCriadoDesc(usuario.getId());
    }

    public List<Notificacao> listarTodas(Usuario usuario) {
        if (usuario == null) return List.of();
        return notificacaoRepository.findByUsuarioIdOrderByCriadoDesc(usuario.getId());
    }

    public long contarNaoLidas(Usuario usuario) {
        if (usuario == null) return 0;
        return notificacaoRepository.countByUsuarioIdAndLidaFalse(usuario.getId());
    }

    @Transactional
    public Notificacao marcarComoLida(Integer id, Usuario usuario) {
        if (id == null || usuario == null) return null;
        Notificacao notif = notificacaoRepository.findById(id).orElse(null);
        if (notif != null && notif.getUsuario().getId().equals(usuario.getId())) {
            notif.setLida(true);
            return notificacaoRepository.save(notif);
        }
        return notif;
    }

    @Transactional
    public void marcarTodasComoLidas(Usuario usuario) {
        if (usuario != null) {
            notificacaoRepository.marcarTodasComoLidas(usuario.getId());
        }
    }
}

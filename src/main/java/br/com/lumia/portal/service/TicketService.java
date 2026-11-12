package br.com.lumia.portal.service;

import br.com.lumia.portal.domain.entity.*;
import br.com.lumia.portal.domain.enums.DecisaoAprovacao;
import br.com.lumia.portal.domain.enums.Perfil;
import br.com.lumia.portal.domain.enums.StatusTicket;
import br.com.lumia.portal.domain.enums.TipoCampo;
import br.com.lumia.portal.domain.enums.TipoInteracao;
import br.com.lumia.portal.repository.AprovacaoRepository;
import br.com.lumia.portal.repository.InteracaoTicketRepository;
import br.com.lumia.portal.repository.RespostaFormularioRepository;
import br.com.lumia.portal.repository.TicketRepository;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final RespostaFormularioRepository respostaRepository;
    private final InteracaoTicketRepository interacaoRepository;
    private final AprovacaoRepository aprovacaoRepository;
    private final TipoSolicitacaoService tipoSolicitacaoService;
    private final FileStorageService fileStorageService;
    private final NotificacaoService notificacaoService;

    public TicketService(TicketRepository ticketRepository,
                         RespostaFormularioRepository respostaRepository,
                         InteracaoTicketRepository interacaoRepository,
                         AprovacaoRepository aprovacaoRepository,
                         TipoSolicitacaoService tipoSolicitacaoService,
                         FileStorageService fileStorageService,
                         NotificacaoService notificacaoService) {
        this.ticketRepository = ticketRepository;
        this.respostaRepository = respostaRepository;
        this.interacaoRepository = interacaoRepository;
        this.aprovacaoRepository = aprovacaoRepository;
        this.tipoSolicitacaoService = tipoSolicitacaoService;
        this.fileStorageService = fileStorageService;
        this.notificacaoService = notificacaoService;
    }

    public Ticket buscarPorId(Integer id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket não encontrado com o código: " + String.format("REQ%07d", id)));
    }

    public List<Ticket> listarPorColaborador(Integer colaboradorId, StatusTicket statusFiltro) {
        if (statusFiltro != null) {
            return ticketRepository.findByColaboradorIdAndStatusOrderByCriadoDesc(colaboradorId, statusFiltro);
        }
        return ticketRepository.findByColaboradorIdOrderByCriadoDesc(colaboradorId);
    }

    public List<Ticket> listarRecentesColaborador(Integer colaboradorId, int limit) {
        List<Ticket> tickets = ticketRepository.findByColaboradorIdOrderByCriadoDesc(colaboradorId);
        if (tickets.size() > limit) {
            return tickets.subList(0, limit);
        }
        return tickets;
    }

    public List<Ticket> listarMeuTrabalho(Integer analistaId) {
        return ticketRepository.findMeuTrabalho(analistaId);
    }

    public List<Ticket> listarMeuTrabalho(Integer analistaId, StatusTicket status) {
        if (status == null) {
            return ticketRepository.findMeuTrabalho(analistaId);
        }
        return ticketRepository.findMeuTrabalhoPorStatus(analistaId, status);
    }

    public List<Ticket> listarAbertos() {
        return ticketRepository.findTicketsEmAberto();
    }

    public List<Ticket> listarEmAndamento() {
        return ticketRepository.findTicketsEmAndamento();
    }

    public List<Ticket> listarPendentes() {
        return ticketRepository.findTicketsPendentes();
    }

    public List<Ticket> listarEncerrados() {
        return ticketRepository.findTicketsEncerrados();
    }

    public List<Ticket> listarEncerradosPorAnalista(Integer analistaId) {
        return ticketRepository.findByStatusInAndAnalistaIdOrderByAtualizadoDesc(
                Arrays.asList(StatusTicket.CONCLUIDO, StatusTicket.REPROVADO), analistaId);
    }

    public List<Ticket> listarPendentesPorAnalista(Integer analistaId) {
        return ticketRepository.findByStatusAndAnalistaIdOrderByCriadoDesc(StatusTicket.PENDENTE, analistaId);
    }

    public long contarPorStatus(StatusTicket status) {
        return ticketRepository.countByStatus(status);
    }

    public long contarEncerrados() {
        return ticketRepository.countByStatusIn(Arrays.asList(StatusTicket.CONCLUIDO, StatusTicket.REPROVADO));
    }

    public long contarAbertosSemAnalista() {
        return ticketRepository.countByStatusAndAnalistaIsNull(StatusTicket.ABERTO);
    }

    public long contarAndamentoPorAnalista(Integer analistaId) {
        return ticketRepository.countByStatusAndAnalistaId(StatusTicket.EM_ANDAMENTO, analistaId);
    }

    public long contarPendentesPorAnalista(Integer analistaId) {
        return ticketRepository.countByStatusAndAnalistaId(StatusTicket.PENDENTE, analistaId);
    }

    public long contarEncerradosPorAnalista(Integer analistaId) {
        return ticketRepository.countByStatusInAndAnalistaId(
                Arrays.asList(StatusTicket.CONCLUIDO, StatusTicket.REPROVADO), analistaId);
    }

    public void validarAcessoTicket(Ticket ticket, Usuario usuario) {
        if (usuario == null) {
            throw new AccessDeniedException("Usuário não autenticado.");
        }
        // RH tem acesso amplo ao workspace
        if (usuario.isRh()) {
            return;
        }
        // Se for o colaborador que abriu o ticket
        if (ticket.getColaborador().getId().equals(usuario.getId())) {
            return;
        }
        // Se for o gestor com aprovação vinculada
        boolean isAprovador = ticket.getAprovacoes().stream()
                .anyMatch(a -> a.getAprovador().getId().equals(usuario.getId()));
        if (isAprovador) {
            return;
        }

        throw new AccessDeniedException("Você não possui permissão para visualizar este chamado.");
    }

    @Transactional
    public Ticket abrirTicket(Usuario colaborador,
                              Integer tipoSolicitacaoId,
                              Map<String, String> respostasTexto,
                              Map<String, MultipartFile> arquivos) {

        TipoSolicitacao tipo = tipoSolicitacaoService.buscarPorId(tipoSolicitacaoId);
        if (!Boolean.TRUE.equals(tipo.getAtivo())) {
            throw new IllegalArgumentException("Este tipo de solicitação está temporariamente inativo.");
        }

        // Validação dos campos obrigatórios
        List<CampoFormulario> campos = tipo.getCampos();
        for (CampoFormulario campo : campos) {
            String campoKey = "campo_" + campo.getId();
            if (Boolean.TRUE.equals(campo.getObrigatorio())) {
                if (TipoCampo.ANEXO.equals(campo.getTipoCampo())) {
                    MultipartFile file = arquivos != null ? arquivos.get(campoKey) : null;
                    if (file == null || file.isEmpty()) {
                        throw new IllegalArgumentException("O anexo '" + campo.getRotulo() + "' é obrigatório.");
                    }
                } else {
                    String valor = respostasTexto != null ? respostasTexto.get(campoKey) : null;
                    if (valor == null || valor.trim().isEmpty()) {
                        throw new IllegalArgumentException("O campo '" + campo.getRotulo() + "' é obrigatório.");
                    }
                }
            }
        }

        Ticket ticket = new Ticket();
        ticket.setColaborador(colaborador);
        ticket.setTipoSolicitacao(tipo);
        ticket.setCriado(LocalDateTime.now());
        ticket.setAtualizado(LocalDateTime.now());

        // Regra de aprovação prévia
        boolean precisaAprovacao = Boolean.TRUE.equals(tipo.getRequerAprovacao());
        Usuario gestor = colaborador.getGestor();

        if (precisaAprovacao && gestor != null) {
            ticket.setStatus(StatusTicket.PENDENTE);
        } else {
            ticket.setStatus(StatusTicket.ABERTO);
        }

        ticket = ticketRepository.save(ticket);

        // Se requer aprovação e tem gestor, cria a pendência na aba do gestor
        if (precisaAprovacao && gestor != null) {
            Aprovacao aprovacao = new Aprovacao();
            aprovacao.setTicket(ticket);
            aprovacao.setAprovador(gestor);
            aprovacao.setDecisao(DecisaoAprovacao.PENDENTE);
            aprovacao.setCriado(LocalDateTime.now());
            aprovacaoRepository.save(aprovacao);

            // Registra interação no histórico
            InteracaoTicket interacao = new InteracaoTicket();
            interacao.setTicket(ticket);
            interacao.setAutor(colaborador);
            interacao.setTipo(TipoInteracao.COMENTARIO);
            interacao.setConteudo("Ticket aberto e aguardando aprovação prévia do gestor imediato: " + gestor.getNome() + ".");
            interacao.setStatusPara(StatusTicket.PENDENTE);
            interacaoRepository.save(interacao);
        } else {
            // Registra interação inicial de abertura
            InteracaoTicket interacao = new InteracaoTicket();
            interacao.setTicket(ticket);
            interacao.setAutor(colaborador);
            interacao.setTipo(TipoInteracao.COMENTARIO);
            interacao.setConteudo("Ticket aberto e disponível para atendimento do RH.");
            interacao.setStatusPara(StatusTicket.ABERTO);
            interacaoRepository.save(interacao);
        }

        // Salvar respostas dos campos
        for (CampoFormulario campo : campos) {
            String campoKey = "campo_" + campo.getId();
            RespostaFormulario resposta = new RespostaFormulario();
            resposta.setTicket(ticket);
            resposta.setCampo(campo);

            if (TipoCampo.ANEXO.equals(campo.getTipoCampo())) {
                MultipartFile file = arquivos != null ? arquivos.get(campoKey) : null;
                if (file != null && !file.isEmpty()) {
                    String storedName = fileStorageService.storeFile(file);
                    resposta.setCaminhoArquivo(storedName);
                }
            } else {
                String valor = respostasTexto != null ? respostasTexto.get(campoKey) : null;
                if (valor != null && !valor.trim().isEmpty()) {
                    resposta.setValorTexto(valor.trim());
                }
            }

            respostaRepository.save(resposta);
            ticket.addResposta(resposta);
        }

        return ticket;
    }

    @Transactional
    public InteracaoTicket adicionarComentario(Integer ticketId, Usuario autor, String conteudo) {
        Ticket ticket = buscarPorId(ticketId);
        validarAcessoTicket(ticket, autor);

        if (ticket.isFinalizado()) {
            throw new IllegalArgumentException("Este chamado encontra-se encerrado (" + ticket.getStatus().getDescricao() + "). Não é permitido adicionar novos comentários.");
        }

        InteracaoTicket interacao = new InteracaoTicket();
        interacao.setTicket(ticket);
        interacao.setAutor(autor);
        interacao.setTipo(autor.isRh() ? TipoInteracao.DEVOLUTIVA : TipoInteracao.COMENTARIO);
        interacao.setConteudo(conteudo.trim());
        interacao.setCriado(LocalDateTime.now());

        ticket.setAtualizado(LocalDateTime.now());
        ticketRepository.save(ticket);

        InteracaoTicket salva = interacaoRepository.save(interacao);
        notificacaoService.notificarAtualizacaoTicket(ticket, autor,
                "Novo(a) " + (autor.isRh() ? "devolutiva" : "comentário") + " registrado(a) por " + autor.getNome() + ".");
        return salva;
    }

    @Transactional
    public void atribuirAnalista(Integer ticketId, Usuario analista) {
        Ticket ticket = buscarPorId(ticketId);
        ticket.setAnalista(analista);

        if (ticket.getStatus() == StatusTicket.ABERTO) {
            ticket.setStatus(StatusTicket.EM_ANDAMENTO);
        }

        ticket.setAtualizado(LocalDateTime.now());
        ticketRepository.save(ticket);

        InteracaoTicket interacao = new InteracaoTicket();
        interacao.setTicket(ticket);
        interacao.setAutor(analista);
        interacao.setTipo(TipoInteracao.MUDANCA_STATUS);
        interacao.setConteudo("Ticket atribuído ao analista de RH " + analista.getNome() + ".");
        interacao.setStatusPara(ticket.getStatus());
        interacao.setCriado(LocalDateTime.now());
        interacaoRepository.save(interacao);

        notificacaoService.notificarAtualizacaoTicket(ticket, analista,
                "O analista " + analista.getNome() + " assumiu o atendimento do seu chamado.");
    }

    @Transactional
    public void alterarStatus(Integer ticketId, Usuario analista, StatusTicket novoStatus, String justificativa) {
        Ticket ticket = buscarPorId(ticketId);
        StatusTicket statusAnterior = ticket.getStatus();
        ticket.setStatus(novoStatus);
        ticket.setAtualizado(LocalDateTime.now());
        ticketRepository.save(ticket);

        InteracaoTicket interacao = new InteracaoTicket();
        interacao.setTicket(ticket);
        interacao.setAutor(analista);
        interacao.setTipo(TipoInteracao.MUDANCA_STATUS);
        String msg = "Status alterado de '" + statusAnterior.getDescricao() + "' para '" + novoStatus.getDescricao() + "'.";
        if (justificativa != null && !justificativa.isBlank()) {
            msg += " Motivo / Observação: " + justificativa.trim();
        }
        interacao.setConteudo(msg);
        interacao.setStatusPara(novoStatus);
        interacao.setCriado(LocalDateTime.now());
        interacaoRepository.save(interacao);

        notificacaoService.notificarAtualizacaoTicket(ticket, analista,
                "Status do chamado alterado para '" + novoStatus.getDescricao() + "'.");
    }

    public Resource carregarAnexo(Integer ticketId, Integer respostaId, Usuario usuario) {
        Ticket ticket = buscarPorId(ticketId);
        validarAcessoTicket(ticket, usuario);

        RespostaFormulario resposta = respostaRepository.findById(respostaId)
                .orElseThrow(() -> new IllegalArgumentException("Resposta não encontrada com ID: " + respostaId));

        if (!resposta.getTicket().getId().equals(ticketId)) {
            throw new IllegalArgumentException("Resposta não pertence ao ticket indicado.");
        }

        if (resposta.getCaminhoArquivo() == null || resposta.getCaminhoArquivo().isBlank()) {
            throw new IllegalArgumentException("Não há anexo registrado para este campo.");
        }

        return fileStorageService.loadFileAsResource(resposta.getCaminhoArquivo());
    }
}

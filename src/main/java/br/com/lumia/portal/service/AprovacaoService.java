package br.com.lumia.portal.service;

import br.com.lumia.portal.domain.entity.Aprovacao;
import br.com.lumia.portal.domain.entity.InteracaoTicket;
import br.com.lumia.portal.domain.entity.Ticket;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.DecisaoAprovacao;
import br.com.lumia.portal.domain.enums.StatusTicket;
import br.com.lumia.portal.domain.enums.TipoInteracao;
import br.com.lumia.portal.repository.AprovacaoRepository;
import br.com.lumia.portal.repository.InteracaoTicketRepository;
import br.com.lumia.portal.repository.TicketRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AprovacaoService {

    private final AprovacaoRepository aprovacaoRepository;
    private final TicketRepository ticketRepository;
    private final InteracaoTicketRepository interacaoRepository;

    public AprovacaoService(AprovacaoRepository aprovacaoRepository,
                            TicketRepository ticketRepository,
                            InteracaoTicketRepository interacaoRepository) {
        this.aprovacaoRepository = aprovacaoRepository;
        this.ticketRepository = ticketRepository;
        this.interacaoRepository = interacaoRepository;
    }

    public List<Aprovacao> listarPendentes(Usuario gestor) {
        if (gestor == null) return List.of();
        return aprovacaoRepository.findByAprovadorIdAndDecisaoOrderByCriadoDesc(gestor.getId(), DecisaoAprovacao.PENDENTE);
    }

    public List<Aprovacao> listarHistoricoDecididas(Usuario gestor) {
        if (gestor == null) return List.of();
        return aprovacaoRepository.findByAprovadorIdOrderByCriadoDesc(gestor.getId()).stream()
                .filter(a -> a.getDecisao() != DecisaoAprovacao.PENDENTE)
                .collect(Collectors.toList());
    }

    public Aprovacao buscarPorId(Integer id) {
        return aprovacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro de aprovação não encontrado com ID: " + id));
    }

    @Transactional
    public void decidir(Integer aprovacaoId, Usuario gestor, DecisaoAprovacao decisao, String comentario) {
        Aprovacao aprovacao = buscarPorId(aprovacaoId);

        // Validação de segurança: apenas o aprovador designado pode decidir
        if (!aprovacao.getAprovador().getId().equals(gestor.getId()) && !gestor.isRh()) {
            throw new AccessDeniedException("Você não possui permissão para aprovar ou rejeitar esta solicitação.");
        }

        if (decisao == DecisaoAprovacao.REJEITADO && (comentario == null || comentario.isBlank())) {
            throw new IllegalArgumentException("É obrigatório informar uma justificativa ao reprovar a solicitação.");
        }

        aprovacao.setDecisao(decisao);
        aprovacao.setComentario(comentario != null ? comentario.trim() : null);
        aprovacao.setDecididoEm(LocalDateTime.now());
        aprovacaoRepository.save(aprovacao);

        Ticket ticket = aprovacao.getTicket();
        ticket.setAtualizado(LocalDateTime.now());

        InteracaoTicket interacao = new InteracaoTicket();
        interacao.setTicket(ticket);
        interacao.setAutor(gestor);
        interacao.setTipo(TipoInteracao.MUDANCA_STATUS);
        interacao.setCriado(LocalDateTime.now());

        if (decisao == DecisaoAprovacao.APROVADO) {
            // RF06: Se aprovado, o atendimento prossegue para a fila do RH
            ticket.setStatus(StatusTicket.ABERTO);
            String texto = "Solicitação APROVADA pela gestão imediata (" + gestor.getNome() + "). O chamado foi encaminhado para atendimento do RH.";
            if (comentario != null && !comentario.isBlank()) {
                texto += " Parecer: " + comentario.trim();
            }
            interacao.setConteudo(texto);
            interacao.setStatusPara(StatusTicket.ABERTO);
        } else {
            // RF06: Se rejeitado, o ticket é encerrado com status Reprovado
            ticket.setStatus(StatusTicket.REPROVADO);
            String texto = "Solicitação REPROVADA pela gestão imediata (" + gestor.getNome() + "). Motivo: " + comentario.trim();
            interacao.setConteudo(texto);
            interacao.setStatusPara(StatusTicket.REPROVADO);
        }

        ticketRepository.save(ticket);
        interacaoRepository.save(interacao);
    }
}

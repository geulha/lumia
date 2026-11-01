package br.com.lumia.portal.domain.entity;

import br.com.lumia.portal.domain.enums.DecisaoAprovacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "aprovacao")
public class Aprovacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprovador_id", nullable = false)
    private Usuario aprovador;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "decisao", nullable = false, length = 20)
    private DecisaoAprovacao decisao = DecisaoAprovacao.PENDENTE;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "decidido_em")
    private LocalDateTime decididoEm;

    @Column(name = "criado", nullable = false, updatable = false)
    private LocalDateTime criado = LocalDateTime.now();

    public Aprovacao() {
    }

    public Aprovacao(Integer id, Ticket ticket, Usuario aprovador) {
        this.id = id;
        this.ticket = ticket;
        this.aprovador = aprovador;
        this.decisao = DecisaoAprovacao.PENDENTE;
        this.criado = LocalDateTime.now();
    }

    public boolean isPendente() {
        return DecisaoAprovacao.PENDENTE.equals(this.decisao);
    }

    public String getDataHoraFormatada() {
        if (decididoEm != null) {
            return decididoEm.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"));
        }
        if (criado != null) {
            return criado.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"));
        }
        return "";
    }

    public String getDataFormatada() {
        if (decididoEm != null) {
            return decididoEm.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        if (criado != null) {
            return criado.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return "";
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Usuario getAprovador() {
        return aprovador;
    }

    public void setAprovador(Usuario aprovador) {
        this.aprovador = aprovador;
    }

    public DecisaoAprovacao getDecisao() {
        return decisao;
    }

    public void setDecisao(DecisaoAprovacao decisao) {
        this.decisao = decisao;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getDecididoEm() {
        return decididoEm;
    }

    public void setDecididoEm(LocalDateTime decididoEm) {
        this.decididoEm = decididoEm;
    }

    public LocalDateTime getCriado() {
        return criado;
    }

    public void setCriado(LocalDateTime criado) {
        this.criado = criado;
    }
}

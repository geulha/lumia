package br.com.lumia.portal.domain.entity;

import br.com.lumia.portal.domain.enums.StatusTicket;
import br.com.lumia.portal.domain.enums.TipoInteracao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "interacao_ticket")
public class InteracaoTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoInteracao tipo;

    @NotBlank
    @Column(name = "conteudo", columnDefinition = "TEXT", nullable = false)
    private String conteudo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_para", length = 20)
    private StatusTicket statusPara;

    @Column(name = "criado", nullable = false, updatable = false)
    private LocalDateTime criado = LocalDateTime.now();

    public InteracaoTicket() {
    }

    public InteracaoTicket(Integer id, Ticket ticket, Usuario autor, TipoInteracao tipo, String conteudo, StatusTicket statusPara) {
        this.id = id;
        this.ticket = ticket;
        this.autor = autor;
        this.tipo = tipo;
        this.conteudo = conteudo;
        this.statusPara = statusPara;
        this.criado = LocalDateTime.now();
    }

    public String getDataHoraFormatada() {
        if (criado == null) return "";
        return criado.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"));
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

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    public TipoInteracao getTipo() {
        return tipo;
    }

    public void setTipo(TipoInteracao tipo) {
        this.tipo = tipo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public StatusTicket getStatusPara() {
        return statusPara;
    }

    public void setStatusPara(StatusTicket statusPara) {
        this.statusPara = statusPara;
    }

    public LocalDateTime getCriado() {
        return criado;
    }

    public void setCriado(LocalDateTime criado) {
        this.criado = criado;
    }
}

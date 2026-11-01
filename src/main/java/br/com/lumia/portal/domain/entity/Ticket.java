package br.com.lumia.portal.domain.entity;

import br.com.lumia.portal.domain.enums.StatusTicket;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colaborador_id", nullable = false)
    private Usuario colaborador;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_solicitacao_id", nullable = false)
    private TipoSolicitacao tipoSolicitacao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusTicket status = StatusTicket.ABERTO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analista_id")
    private Usuario analista;

    @Column(name = "criado", nullable = false, updatable = false)
    private LocalDateTime criado = LocalDateTime.now();

    @Column(name = "atualizado", nullable = false)
    private LocalDateTime atualizado = LocalDateTime.now();

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RespostaFormulario> respostas = new ArrayList<>();

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("criado ASC")
    private List<InteracaoTicket> interacoes = new ArrayList<>();

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("criado DESC")
    private List<Aprovacao> aprovacoes = new ArrayList<>();

    public Ticket() {
    }

    public Ticket(Integer id, Usuario colaborador, TipoSolicitacao tipoSolicitacao, StatusTicket status, Usuario analista) {
        this.id = id;
        this.colaborador = colaborador;
        this.tipoSolicitacao = tipoSolicitacao;
        this.status = status != null ? status : StatusTicket.ABERTO;
        this.analista = analista;
        this.criado = LocalDateTime.now();
        this.atualizado = LocalDateTime.now();
    }

    public String getCodigoFormatado() {
        if (id == null) return "REQ0000000";
        return String.format("REQ%07d", id);
    }

    public String getDataFormatada() {
        if (criado == null) return "";
        return criado.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getDataHoraFormatada() {
        if (criado == null) return "";
        return criado.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getTempoRelativo() {
        if (criado == null) return "";
        LocalDateTime agora = LocalDateTime.now();
        Duration duracao = Duration.between(criado, agora);
        long minutos = duracao.toMinutes();
        long horas = duracao.toHours();
        long dias = duracao.toDays();

        if (minutos < 1) return "agora mesmo";
        if (minutos == 1) return "há 1 minuto";
        if (minutos < 60) return minutos + " minutos atrás";
        if (horas == 1) return "há uma hora";
        if (horas < 24) return "há " + horas + " horas";
        if (dias == 1) return "um dia atrás";
        if (dias < 7) return "há " + dias + " dias";
        if (dias < 14) return "há uma semana";
        return getDataFormatada();
    }

    public void addResposta(RespostaFormulario resposta) {
        respostas.add(resposta);
        resposta.setTicket(this);
    }

    public void addInteracao(InteracaoTicket interacao) {
        interacoes.add(interacao);
        interacao.setTicket(this);
        this.atualizado = LocalDateTime.now();
    }

    public void addAprovacao(Aprovacao aprovacao) {
        aprovacoes.add(aprovacao);
        aprovacao.setTicket(this);
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getColaborador() {
        return colaborador;
    }

    public void setColaborador(Usuario colaborador) {
        this.colaborador = colaborador;
    }

    public TipoSolicitacao getTipoSolicitacao() {
        return tipoSolicitacao;
    }

    public void setTipoSolicitacao(TipoSolicitacao tipoSolicitacao) {
        this.tipoSolicitacao = tipoSolicitacao;
    }

    public StatusTicket getStatus() {
        return status;
    }

    public void setStatus(StatusTicket status) {
        this.status = status;
        this.atualizado = LocalDateTime.now();
    }

    public Usuario getAnalista() {
        return analista;
    }

    public void setAnalista(Usuario analista) {
        this.analista = analista;
        this.atualizado = LocalDateTime.now();
    }

    public LocalDateTime getCriado() {
        return criado;
    }

    public void setCriado(LocalDateTime criado) {
        this.criado = criado;
    }

    public LocalDateTime getAtualizado() {
        return atualizado;
    }

    public void setAtualizado(LocalDateTime atualizado) {
        this.atualizado = atualizado;
    }

    public List<RespostaFormulario> getRespostas() {
        return respostas;
    }

    public void setRespostas(List<RespostaFormulario> respostas) {
        this.respostas = respostas;
    }

    public List<InteracaoTicket> getInteracoes() {
        return interacoes;
    }

    public void setInteracoes(List<InteracaoTicket> interacoes) {
        this.interacoes = interacoes;
    }

    public List<Aprovacao> getAprovacoes() {
        return aprovacoes;
    }

    public void setAprovacoes(List<Aprovacao> aprovacoes) {
        this.aprovacoes = aprovacoes;
    }
}

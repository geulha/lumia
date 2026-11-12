package br.com.lumia.portal.domain.entity;

import br.com.lumia.portal.domain.enums.TipoNotificacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "notificacao")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotBlank
    @Size(max = 150)
    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @NotBlank
    @Column(name = "mensagem", columnDefinition = "TEXT", nullable = false)
    private String mensagem;

    @Size(max = 255)
    @Column(name = "link", length = 255)
    private String link;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 50)
    private TipoNotificacao tipo = TipoNotificacao.TICKET_ATUALIZADO;

    @Column(name = "lida", nullable = false)
    private Boolean lida = false;

    @Column(name = "criado", nullable = false, updatable = false)
    private LocalDateTime criado = LocalDateTime.now();

    public Notificacao() {
    }

    public Notificacao(Usuario usuario, String titulo, String mensagem, String link, TipoNotificacao tipo) {
        this.usuario = usuario;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.link = link;
        this.tipo = tipo != null ? tipo : TipoNotificacao.TICKET_ATUALIZADO;
        this.lida = false;
        this.criado = LocalDateTime.now();
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
        if (minutos < 60) return "há " + minutos + " min";
        if (horas == 1) return "há 1 hora";
        if (horas < 24) return "há " + horas + " h";
        if (dias == 1) return "ontem";
        if (dias < 7) return "há " + dias + " dias";
        return criado.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public TipoNotificacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoNotificacao tipo) {
        this.tipo = tipo;
    }

    public Boolean getLida() {
        return lida;
    }

    public void setLida(Boolean lida) {
        this.lida = lida;
    }

    public LocalDateTime getCriado() {
        return criado;
    }

    public void setCriado(LocalDateTime criado) {
        this.criado = criado;
    }
}

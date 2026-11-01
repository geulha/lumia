package br.com.lumia.portal.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "artigo")
public class Artigo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 255)
    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    @NotBlank(message = "O conteúdo é obrigatório")
    @Column(name = "conteudo", columnDefinition = "TEXT", nullable = false)
    private String conteudo;

    @NotBlank(message = "A categoria é obrigatória")
    @Size(max = 100)
    @Column(name = "categoria", nullable = false, length = 100)
    private String categoria;

    @Column(name = "publicado", nullable = false)
    private Boolean publicado = true;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @Column(name = "criado", nullable = false, updatable = false)
    private LocalDateTime criado = LocalDateTime.now();

    @Column(name = "atualizado", nullable = false)
    private LocalDateTime atualizado = LocalDateTime.now();

    public Artigo() {
    }

    public Artigo(Integer id, String titulo, String conteudo, String categoria, Boolean publicado, Usuario autor) {
        this.id = id;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.categoria = categoria;
        this.publicado = publicado != null ? publicado : true;
        this.autor = autor;
        this.criado = LocalDateTime.now();
        this.atualizado = LocalDateTime.now();
    }

    public String getCodigoFormatado() {
        if (id == null) return "ART0000";
        return String.format("ART%04d", id);
    }

    public String getDataFormatada() {
        if (criado == null) return "";
        return criado.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getTempoRelativo() {
        if (criado == null) return "";
        LocalDateTime agora = LocalDateTime.now();
        Duration duracao = Duration.between(criado, agora);
        long minutos = duracao.toMinutes();
        long horas = duracao.toHours();
        long dias = duracao.toDays();

        if (minutos < 60) return minutos <= 1 ? "agora há pouco" : minutos + " minutos atrás";
        if (horas == 1) return "uma hora atrás";
        if (horas < 24) return horas + " horas atrás";
        if (dias == 1) return "um dia atrás";
        if (dias < 7) return dias + " dias atrás";
        return getDataFormatada();
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Boolean getPublicado() {
        return publicado;
    }

    public void setPublicado(Boolean publicado) {
        this.publicado = publicado;
        this.atualizado = LocalDateTime.now();
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
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
}

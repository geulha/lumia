package br.com.lumia.portal.dto;

import br.com.lumia.portal.domain.enums.Perfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ArtigoForm {

    private Integer id;

    @NotBlank(message = "O título do artigo é obrigatório")
    @Size(max = 255)
    private String titulo;

    @NotBlank(message = "O conteúdo é obrigatório")
    private String conteudo;

    @NotBlank(message = "A categoria é obrigatória")
    @Size(max = 100)
    private String categoria;

    private Boolean publicado = true;

    private Perfil visivelParaPerfil;

    @Size(max = 100)
    private String visivelParaPosto;

    @Size(max = 50)
    private String icone = "bi-file-earmark-text";

    public ArtigoForm() {
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
    }

    public Perfil getVisivelParaPerfil() {
        return visivelParaPerfil;
    }

    public void setVisivelParaPerfil(Perfil visivelParaPerfil) {
        this.visivelParaPerfil = visivelParaPerfil;
    }

    public String getVisivelParaPosto() {
        return visivelParaPosto;
    }

    public void setVisivelParaPosto(String visivelParaPosto) {
        this.visivelParaPosto = visivelParaPosto;
    }

    public String getIcone() {
        return icone != null && !icone.isBlank() ? icone : "bi-file-earmark-text";
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }
}

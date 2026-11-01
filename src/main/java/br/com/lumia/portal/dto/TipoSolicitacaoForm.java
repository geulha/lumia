package br.com.lumia.portal.dto;

import br.com.lumia.portal.domain.enums.Perfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TipoSolicitacaoForm {

    private Integer id;

    @NotBlank(message = "O nome do formulário é obrigatório")
    @Size(max = 250, message = "O nome não pode exceder 250 caracteres")
    private String nome;

    private String descricao;

    private Boolean requerAprovacao = false;

    private Perfil visivelParaPerfil;

    @Size(max = 100)
    private String visivelParaPosto;

    @NotBlank(message = "A categoria é obrigatória")
    @Size(max = 100)
    private String categoria;

    private Boolean ativo = true;

    public TipoSolicitacaoForm() {
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getRequerAprovacao() {
        return requerAprovacao;
    }

    public void setRequerAprovacao(Boolean requerAprovacao) {
        this.requerAprovacao = requerAprovacao;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}

package br.com.lumia.portal.domain.entity;

import br.com.lumia.portal.domain.enums.Perfil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tipo_solicitacao")
public class TipoSolicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "O nome da solicitação é obrigatório")
    @Size(max = 250)
    @Column(name = "nome", nullable = false, length = 250)
    private String nome;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "requer_aprovacao", nullable = false)
    private Boolean requerAprovacao = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "visivel_para_perfil", length = 20)
    private Perfil visivelParaPerfil;

    @Size(max = 100)
    @Column(name = "visivel_para_posto", length = 100)
    private String visivelParaPosto;

    @Size(max = 100)
    @Column(name = "categoria", length = 100)
    private String categoria;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por_id", nullable = false)
    private Usuario criadoPor;

    @Column(name = "criado", nullable = false, updatable = false)
    private LocalDateTime criado = LocalDateTime.now();

    @OneToMany(mappedBy = "tipoSolicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<CampoFormulario> campos = new ArrayList<>();

    public TipoSolicitacao() {
    }

    public TipoSolicitacao(Integer id, String nome, String descricao, Boolean requerAprovacao, Perfil visivelParaPerfil, String visivelParaPosto, String categoria, Usuario criadoPor) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.requerAprovacao = requerAprovacao != null ? requerAprovacao : false;
        this.visivelParaPerfil = visivelParaPerfil;
        this.visivelParaPosto = visivelParaPosto;
        this.categoria = categoria;
        this.criadoPor = criadoPor;
        this.ativo = true;
        this.criado = LocalDateTime.now();
    }

    public void addCampo(CampoFormulario campo) {
        campos.add(campo);
        campo.setTipoSolicitacao(this);
    }

    public void removeCampo(CampoFormulario campo) {
        campos.remove(campo);
        campo.setTipoSolicitacao(null);
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

    public Usuario getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(Usuario criadoPor) {
        this.criadoPor = criadoPor;
    }

    public LocalDateTime getCriado() {
        return criado;
    }

    public void setCriado(LocalDateTime criado) {
        this.criado = criado;
    }

    public List<CampoFormulario> getCampos() {
        return campos;
    }

    public void setCampos(List<CampoFormulario> campos) {
        this.campos = campos;
    }
}

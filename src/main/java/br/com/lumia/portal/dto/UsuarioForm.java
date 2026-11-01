package br.com.lumia.portal.dto;

import br.com.lumia.portal.domain.enums.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UsuarioForm {

    private Integer id;

    @NotBlank(message = "O nome completo é obrigatório")
    @Size(max = 150, message = "O nome não pode exceder 150 caracteres")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Informe um e-mail válido")
    @Size(max = 255)
    private String email;

    @Size(max = 14, message = "CPF inválido")
    private String cpf;

    @NotNull(message = "O perfil de acesso é obrigatório")
    private Perfil perfil = Perfil.COLABORADOR;

    @Size(max = 100, message = "O cargo/posto não pode exceder 100 caracteres")
    private String posto;

    private Integer gestorId;

    private Boolean ativo = true;

    private Boolean bloqueado = false;

    public UsuarioForm() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public String getPosto() {
        return posto;
    }

    public void setPosto(String posto) {
        this.posto = posto;
    }

    public Integer getGestorId() {
        return gestorId;
    }

    public void setGestorId(Integer gestorId) {
        this.gestorId = gestorId;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }
}

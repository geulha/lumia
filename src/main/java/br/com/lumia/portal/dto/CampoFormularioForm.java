package br.com.lumia.portal.dto;

import br.com.lumia.portal.domain.enums.TipoCampo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CampoFormularioForm {

    private Integer id;

    @NotBlank(message = "O rótulo do campo é obrigatório")
    @Size(max = 150)
    private String rotulo;

    @NotNull(message = "O tipo do campo é obrigatório")
    private TipoCampo tipoCampo = TipoCampo.TEXTO;

    private Boolean obrigatorio = false;

    private Integer ordem = 1;

    private String opcoes; // Texto com opções para campos do tipo SELEÇÃO

    public CampoFormularioForm() {
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRotulo() {
        return rotulo;
    }

    public void setRotulo(String rotulo) {
        this.rotulo = rotulo;
    }

    public TipoCampo getTipoCampo() {
        return tipoCampo;
    }

    public void setTipoCampo(TipoCampo tipoCampo) {
        this.tipoCampo = tipoCampo;
    }

    public Boolean getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(Boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getOpcoes() {
        return opcoes;
    }

    public void setOpcoes(String opcoes) {
        this.opcoes = opcoes;
    }
}

package br.com.lumia.portal.domain.entity;

import br.com.lumia.portal.domain.enums.TipoCampo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "campo_formulario")
public class CampoFormulario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_solicitacao_id", nullable = false)
    private TipoSolicitacao tipoSolicitacao;

    @NotBlank(message = "O rótulo do campo é obrigatório")
    @Size(max = 150)
    @Column(name = "rotulo", nullable = false, length = 150)
    private String rotulo;

    @NotNull(message = "O tipo do campo é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_campo", nullable = false, length = 20)
    private TipoCampo tipoCampo;

    @Column(name = "obrigatorio", nullable = false)
    private Boolean obrigatorio = false;

    @Column(name = "ordem", nullable = false)
    private Integer ordem = 0;

    @Column(name = "opcoes", columnDefinition = "TEXT")
    private String opcoes; // Ex.: ["Opção 1", "Opção 2"] ou lista separada por vírgula/linhas

    public CampoFormulario() {
    }

    public CampoFormulario(Integer id, TipoSolicitacao tipoSolicitacao, String rotulo, TipoCampo tipoCampo, Boolean obrigatorio, Integer ordem, String opcoes) {
        this.id = id;
        this.tipoSolicitacao = tipoSolicitacao;
        this.rotulo = rotulo;
        this.tipoCampo = tipoCampo;
        this.obrigatorio = obrigatorio != null ? obrigatorio : false;
        this.ordem = ordem != null ? ordem : 0;
        this.opcoes = opcoes;
    }

    /**
     * Retorna a lista de opções para campos de seleção
     */
    public List<String> getListaOpcoes() {
        if (opcoes == null || opcoes.isBlank()) {
            return new ArrayList<>();
        }
        String cleaned = opcoes.trim();
        if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        return Arrays.stream(cleaned.split("[,;\\n\\r]+"))
                .map(s -> s.replace("\"", "").trim())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TipoSolicitacao getTipoSolicitacao() {
        return tipoSolicitacao;
    }

    public void setTipoSolicitacao(TipoSolicitacao tipoSolicitacao) {
        this.tipoSolicitacao = tipoSolicitacao;
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

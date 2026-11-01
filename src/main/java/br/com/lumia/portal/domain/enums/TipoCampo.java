package br.com.lumia.portal.domain.enums;

public enum TipoCampo {
    TEXTO("Texto Curto"),
    NUMERO("Número"),
    DATA("Data"),
    ANEXO("Anexo de Arquivo"),
    SELECAO("Seleção (Múltipla Escolha)"),
    TEXTO_LONGO("Texto Longo / Descrição");

    private final String descricao;

    TipoCampo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

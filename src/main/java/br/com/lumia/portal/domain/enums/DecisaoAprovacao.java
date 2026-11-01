package br.com.lumia.portal.domain.enums;

public enum DecisaoAprovacao {
    PENDENTE("Pendente", "#E6A100"),
    APROVADO("Aprovado", "#2E7D32"),
    REJEITADO("Rejeitado", "#E53935");

    private final String descricao;
    private final String corHex;

    DecisaoAprovacao(String descricao, String corHex) {
        this.descricao = descricao;
        this.corHex = corHex;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCorHex() {
        return corHex;
    }
}

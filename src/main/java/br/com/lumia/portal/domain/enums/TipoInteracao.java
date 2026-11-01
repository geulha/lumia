package br.com.lumia.portal.domain.enums;

public enum TipoInteracao {
    COMENTARIO("Comentário"),
    MUDANCA_STATUS("Mudança de Status"),
    DEVOLUTIVA("Devolutiva do RH");

    private final String descricao;

    TipoInteracao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

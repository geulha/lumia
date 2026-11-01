package br.com.lumia.portal.domain.enums;

public enum StatusTicket {
    ABERTO("Aberto", "badge-aberto", "#2196F3"),
    EM_ANDAMENTO("Em andamento", "badge-em-andamento", "#FF9800"),
    PENDENTE("Pendente", "badge-pendente", "#E6A100"),
    CONCLUIDO("Concluído", "badge-concluido", "#212121"),
    REPROVADO("Reprovado", "badge-reprovado", "#E53935");

    private final String descricao;
    private final String classeCss;
    private final String corHex;

    StatusTicket(String descricao, String classeCss, String corHex) {
        this.descricao = descricao;
        this.classeCss = classeCss;
        this.corHex = corHex;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getClasseCss() {
        return classeCss;
    }

    public String getCorHex() {
        return corHex;
    }
}

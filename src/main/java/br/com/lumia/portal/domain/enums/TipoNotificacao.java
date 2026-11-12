package br.com.lumia.portal.domain.enums;

public enum TipoNotificacao {
    TICKET_ATUALIZADO("Atualização de Chamado", "bi-ticket-detailed", "#6C5CE7"),
    NOVO_ARTIGO("Novo Artigo Publicado", "bi-journal-bookmark-fill", "#2563EB"),
    NOVO_FORMULARIO("Novo Serviço Disponível", "bi-ui-checks", "#16A34A");

    private final String descricao;
    private final String icone;
    private final String cor;

    TipoNotificacao(String descricao, String icone, String cor) {
        this.descricao = descricao;
        this.icone = icone;
        this.cor = cor;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getIcone() {
        return icone;
    }

    public String getCor() {
        return cor;
    }
}

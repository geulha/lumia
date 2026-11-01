package br.com.lumia.portal.domain.enums;

public enum Perfil {
    COLABORADOR("Colaborador"),
    RH("Profissional de RH");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

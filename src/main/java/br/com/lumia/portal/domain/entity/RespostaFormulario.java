package br.com.lumia.portal.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "resposta_formulario")
public class RespostaFormulario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "campo_id", nullable = false)
    private CampoFormulario campo;

    @Column(name = "valor_texto", columnDefinition = "TEXT")
    private String valorTexto;

    @Column(name = "caminho_arquivo", length = 500)
    private String caminhoArquivo;

    public RespostaFormulario() {
    }

    public RespostaFormulario(Integer id, Ticket ticket, CampoFormulario campo, String valorTexto, String caminhoArquivo) {
        this.id = id;
        this.ticket = ticket;
        this.campo = campo;
        this.valorTexto = valorTexto;
        this.caminhoArquivo = caminhoArquivo;
    }

    public String getNomeArquivoOriginal() {
        if (caminhoArquivo == null || caminhoArquivo.isBlank()) return null;
        // Se estiver no formato "uuid_originalfilename.ext", podemos retornar o nome original
        int idx = caminhoArquivo.indexOf('_');
        if (idx != -1 && idx < caminhoArquivo.length() - 1) {
            return caminhoArquivo.substring(idx + 1);
        }
        return caminhoArquivo;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public CampoFormulario getCampo() {
        return campo;
    }

    public void setCampo(CampoFormulario campo) {
        this.campo = campo;
    }

    public String getValorTexto() {
        return valorTexto;
    }

    public void setValorTexto(String valorTexto) {
        this.valorTexto = valorTexto;
    }

    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public void setCaminhoArquivo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }
}

package br.com.lumia.portal.service;

import br.com.lumia.portal.domain.entity.CampoFormulario;
import br.com.lumia.portal.domain.entity.TipoSolicitacao;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.TipoCampo;
import br.com.lumia.portal.dto.CampoFormularioForm;
import br.com.lumia.portal.dto.TipoSolicitacaoForm;
import br.com.lumia.portal.repository.CampoFormularioRepository;
import br.com.lumia.portal.repository.TipoSolicitacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoSolicitacaoService {

    private final TipoSolicitacaoRepository tipoSolicitacaoRepository;
    private final CampoFormularioRepository campoFormularioRepository;

    public TipoSolicitacaoService(TipoSolicitacaoRepository tipoSolicitacaoRepository,
                                  CampoFormularioRepository campoFormularioRepository) {
        this.tipoSolicitacaoRepository = tipoSolicitacaoRepository;
        this.campoFormularioRepository = campoFormularioRepository;
    }

    public List<TipoSolicitacao> listarTodos() {
        return tipoSolicitacaoRepository.findAllByOrderByNomeAsc();
    }

    public List<TipoSolicitacao> listarAtivos() {
        return tipoSolicitacaoRepository.findByAtivoTrueOrderByNomeAsc();
    }

    public TipoSolicitacao buscarPorId(Integer id) {
        return tipoSolicitacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Formulário não encontrado com o ID: " + id));
    }

    public List<String> listarCategoriasDisponiveis() {
        List<String> doBanco = tipoSolicitacaoRepository.findDistinctCategorias();
        if (doBanco.isEmpty()) {
            return Arrays.asList("Benefícios", "Folha de Pagamento", "Documentos", "Férias");
        }
        return doBanco;
    }

    public List<TipoSolicitacao> listarDisponiveisParaUsuario(Usuario usuario) {
        if (usuario == null) {
            return tipoSolicitacaoRepository.findByAtivoTrueOrderByNomeAsc();
        }
        boolean isRh = usuario.isRh();
        String posto = usuario.getPosto() != null ? usuario.getPosto() : "";
        return tipoSolicitacaoRepository.findDisponiveisParaUsuario(isRh, usuario.getPerfil(), posto);
    }

    public List<TipoSolicitacao> listarDisponiveisPorCategoria(Usuario usuario, String categoria) {
        if (usuario == null) {
            return tipoSolicitacaoRepository.findByAtivoTrueAndCategoriaIgnoreCaseOrderByNomeAsc(categoria);
        }
        boolean isRh = usuario.isRh();
        String posto = usuario.getPosto() != null ? usuario.getPosto() : "";
        return tipoSolicitacaoRepository.findDisponiveisParaUsuarioECategoria(isRh, usuario.getPerfil(), posto, categoria);
    }

    public static String normalizar(String texto) {
        if (texto == null) return "";
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }

    public List<TipoSolicitacao> buscarPorTermo(Usuario usuario, String termo) {
        List<TipoSolicitacao> disponiveis = listarDisponiveisParaUsuario(usuario);
        if (termo == null || termo.isBlank()) {
            return disponiveis;
        }
        String termoNorm = normalizar(termo.trim());
        String[] palavras = termoNorm.split("\\s+");
        return disponiveis.stream()
                .filter(t -> {
                    String nomeNorm = normalizar(t.getNome());
                    String catNorm = normalizar(t.getCategoria());
                    String descNorm = normalizar(t.getDescricao());
                    return Arrays.stream(palavras).allMatch(p ->
                            nomeNorm.contains(p) || catNorm.contains(p) || descNorm.contains(p));
                })
                .toList();
    }

    @Transactional
    public TipoSolicitacao criar(TipoSolicitacaoForm form, Usuario criadoPor) {
        TipoSolicitacao tipo = new TipoSolicitacao();
        tipo.setNome(form.getNome().trim());
        tipo.setDescricao(form.getDescricao() != null ? form.getDescricao().trim() : null);
        tipo.setCategoria(form.getCategoria().trim());
        tipo.setRequerAprovacao(form.getRequerAprovacao() != null ? form.getRequerAprovacao() : false);
        tipo.setVisivelParaPerfil(form.getVisivelParaPerfil());
        tipo.setVisivelParaPosto(form.getVisivelParaPosto() != null && !form.getVisivelParaPosto().isBlank() ? form.getVisivelParaPosto().trim() : null);
        tipo.setAtivo(form.getAtivo() != null ? form.getAtivo() : true);
        tipo.setCriadoPor(criadoPor);
        tipo.setCriado(LocalDateTime.now());

        return tipoSolicitacaoRepository.save(tipo);
    }

    @Transactional
    public void atualizar(Integer id, TipoSolicitacaoForm form) {
        TipoSolicitacao tipo = buscarPorId(id);
        tipo.setNome(form.getNome().trim());
        tipo.setDescricao(form.getDescricao() != null ? form.getDescricao().trim() : null);
        tipo.setCategoria(form.getCategoria().trim());
        tipo.setRequerAprovacao(form.getRequerAprovacao() != null ? form.getRequerAprovacao() : false);
        tipo.setVisivelParaPerfil(form.getVisivelParaPerfil());
        tipo.setVisivelParaPosto(form.getVisivelParaPosto() != null && !form.getVisivelParaPosto().isBlank() ? form.getVisivelParaPosto().trim() : null);
        if (form.getAtivo() != null) tipo.setAtivo(form.getAtivo());

        tipoSolicitacaoRepository.save(tipo);
    }

    @Transactional
    public boolean alternarStatus(Integer id) {
        TipoSolicitacao tipo = buscarPorId(id);
        tipo.setAtivo(!Boolean.TRUE.equals(tipo.getAtivo()));
        tipoSolicitacaoRepository.save(tipo);
        return tipo.getAtivo();
    }

    @Transactional
    public CampoFormulario adicionarCampo(Integer tipoSolicitacaoId, CampoFormularioForm campoForm) {
        TipoSolicitacao tipo = buscarPorId(tipoSolicitacaoId);

        CampoFormulario campo = new CampoFormulario();
        campo.setTipoSolicitacao(tipo);
        campo.setRotulo(campoForm.getRotulo().trim());
        campo.setTipoCampo(campoForm.getTipoCampo());
        campo.setObrigatorio(campoForm.getObrigatorio() != null ? campoForm.getObrigatorio() : false);
        campo.setOrdem(campoForm.getOrdem() != null ? campoForm.getOrdem() : (tipo.getCampos().size() + 1));

        if (TipoCampo.SELECAO.equals(campoForm.getTipoCampo()) && campoForm.getOpcoes() != null) {
            String formatado = formatarOpcoesParaJson(campoForm.getOpcoes());
            campo.setOpcoes(formatado);
        } else {
            campo.setOpcoes(null);
        }

        tipo.addCampo(campo);
        return campoFormularioRepository.save(campo);
    }

    @Transactional
    public void excluirCampo(Integer campoId) {
        CampoFormulario campo = campoFormularioRepository.findById(campoId)
                .orElseThrow(() -> new IllegalArgumentException("Campo não encontrado com o ID: " + campoId));
        TipoSolicitacao tipo = campo.getTipoSolicitacao();
        tipo.removeCampo(campo);
        campoFormularioRepository.delete(campo);
    }

    @Transactional
    public void excluir(Integer id) {
        TipoSolicitacao tipo = buscarPorId(id);
        tipoSolicitacaoRepository.delete(tipo);
    }

    private String formatarOpcoesParaJson(String opcoesTexto) {
        if (opcoesTexto == null || opcoesTexto.isBlank()) return "[]";
        List<String> items = Arrays.stream(opcoesTexto.split("[,;\\n\\r]+"))
                .map(s -> s.replace("\"", "").trim())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            sb.append("\"").append(items.get(i)).append("\"");
            if (i < items.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}

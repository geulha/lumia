package br.com.lumia.portal.service;

import br.com.lumia.portal.domain.entity.Artigo;
import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.dto.ArtigoForm;
import br.com.lumia.portal.repository.ArtigoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class ArtigoService {

    private final ArtigoRepository artigoRepository;

    public ArtigoService(ArtigoRepository artigoRepository) {
        this.artigoRepository = artigoRepository;
    }

    public List<Artigo> listarPublicados() {
        return artigoRepository.findByPublicadoTrueOrderByCriadoDesc();
    }

    public List<Artigo> listarPublicadosRecentes(int limit) {
        List<Artigo> artigos = artigoRepository.findByPublicadoTrueOrderByCriadoDesc();
        if (artigos.size() > limit) {
            return artigos.subList(0, limit);
        }
        return artigos;
    }

    public List<Artigo> listarTodos() {
        return artigoRepository.findAllByOrderByCriadoDesc();
    }

    public Artigo buscarPorId(Integer id) {
        return artigoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Artigo não encontrado com ID: " + id));
    }

    public List<Artigo> buscarPublicadosPorCategoria(String categoria) {
        return artigoRepository.findByPublicadoTrueAndCategoriaIgnoreCaseOrderByCriadoDesc(categoria);
    }

    public List<Artigo> buscarPublicadosPorTermo(String termo) {
        if (termo == null || termo.isBlank()) {
            return listarPublicados();
        }
        return artigoRepository.buscarPublicadosPorTermo(termo.trim());
    }

    public List<String> listarCategorias() {
        List<String> categorias = artigoRepository.findDistinctCategorias();
        if (categorias.isEmpty()) {
            return Arrays.asList("Férias", "Folha de Pagamento", "Benefícios", "Documentos");
        }
        return categorias;
    }

    @Transactional
    public Artigo criar(ArtigoForm form, Usuario autor) {
        Artigo artigo = new Artigo();
        artigo.setTitulo(form.getTitulo().trim());
        artigo.setConteudo(form.getConteudo().trim());
        artigo.setCategoria(form.getCategoria().trim());
        artigo.setPublicado(form.getPublicado() != null ? form.getPublicado() : true);
        artigo.setAutor(autor);
        artigo.setCriado(LocalDateTime.now());
        artigo.setAtualizado(LocalDateTime.now());

        return artigoRepository.save(artigo);
    }

    @Transactional
    public void atualizar(Integer id, ArtigoForm form) {
        Artigo artigo = buscarPorId(id);
        artigo.setTitulo(form.getTitulo().trim());
        artigo.setConteudo(form.getConteudo().trim());
        artigo.setCategoria(form.getCategoria().trim());
        if (form.getPublicado() != null) artigo.setPublicado(form.getPublicado());
        artigo.setAtualizado(LocalDateTime.now());

        artigoRepository.save(artigo);
    }

    @Transactional
    public boolean alternarPublicacao(Integer id) {
        Artigo artigo = buscarPorId(id);
        artigo.setPublicado(!Boolean.TRUE.equals(artigo.getPublicado()));
        artigo.setAtualizado(LocalDateTime.now());
        artigoRepository.save(artigo);
        return artigo.getPublicado();
    }

    @Transactional
    public void excluir(Integer id) {
        Artigo artigo = buscarPorId(id);
        artigoRepository.delete(artigo);
    }
}

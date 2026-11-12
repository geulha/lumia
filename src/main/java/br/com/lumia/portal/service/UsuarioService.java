package br.com.lumia.portal.service;

import br.com.lumia.portal.domain.entity.Usuario;
import br.com.lumia.portal.domain.enums.Perfil;
import br.com.lumia.portal.dto.UsuarioForm;
import br.com.lumia.portal.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%";
    private static final SecureRandom RANDOM = new SecureRandom();

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAllByOrderByNomeAsc();
    }

    public Usuario buscarPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com o ID: " + id));
    }

    public List<Usuario> listarPossiveisGestores(Integer usuarioIdAtual) {
        return usuarioRepository.findByAtivoTrueOrderByNomeAsc().stream()
                .filter(u -> usuarioIdAtual == null || !u.getId().equals(usuarioIdAtual))
                .collect(Collectors.toList());
    }

    @Transactional
    public String criar(UsuarioForm form) {
        if (usuarioRepository.existsByEmailIgnoreCase(form.getEmail())) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com o e-mail: " + form.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setNome(form.getNome().trim());
        usuario.setEmail(form.getEmail().trim().toLowerCase());
        usuario.setCpf(form.getCpf() != null ? form.getCpf().replaceAll("\\D", "") : null);
        usuario.setPerfil(form.getPerfil());
        usuario.setPosto(form.getPosto() != null ? form.getPosto().trim() : null);
        usuario.setAtivo(form.getAtivo() != null ? form.getAtivo() : true);
        usuario.setBloqueado(form.getBloqueado() != null ? form.getBloqueado() : false);

        if (form.getGestorId() != null) {
            Usuario gestor = buscarPorId(form.getGestorId());
            usuario.setGestor(gestor);
        }

        // RF01: Gerar senha aleatória temporária
        String senhaTemporaria = gerarSenhaAleatoria(10);
        usuario.setSenhaHash(passwordEncoder.encode(senhaTemporaria));
        usuario.setPrimeiroAcesso(true);

        usuarioRepository.save(usuario);
        return senhaTemporaria;
    }

    @Transactional
    public void atualizar(Integer id, UsuarioForm form) {
        Usuario usuario = buscarPorId(id);

        // Se alterou e-mail, verificar unicidade
        if (!usuario.getEmail().equalsIgnoreCase(form.getEmail().trim())) {
            if (usuarioRepository.existsByEmailIgnoreCase(form.getEmail())) {
                throw new IllegalArgumentException("Já existe outro usuário com o e-mail: " + form.getEmail());
            }
            usuario.setEmail(form.getEmail().trim().toLowerCase());
        }

        usuario.setNome(form.getNome().trim());
        usuario.setCpf(form.getCpf() != null ? form.getCpf().replaceAll("\\D", "") : null);
        usuario.setPerfil(form.getPerfil());
        usuario.setPosto(form.getPosto() != null ? form.getPosto().trim() : null);

        if (form.getGestorId() != null) {
            if (form.getGestorId().equals(id)) {
                throw new IllegalArgumentException("Um usuário não pode ser gestor de si mesmo.");
            }
            Usuario gestor = buscarPorId(form.getGestorId());
            usuario.setGestor(gestor);
        } else {
            usuario.setGestor(null);
        }

        if (form.getAtivo() != null) usuario.setAtivo(form.getAtivo());
        if (form.getBloqueado() != null) usuario.setBloqueado(form.getBloqueado());

        usuarioRepository.save(usuario);
    }

    @Transactional
    public boolean alternarStatusAtivo(Integer id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(!Boolean.TRUE.equals(usuario.getAtivo()));
        usuarioRepository.save(usuario);
        return usuario.getAtivo();
    }

    @Transactional
    public boolean alternarStatusBloqueio(Integer id) {
        Usuario usuario = buscarPorId(id);
        usuario.setBloqueado(!Boolean.TRUE.equals(usuario.getBloqueado()));
        usuarioRepository.save(usuario);
        return usuario.getBloqueado();
    }

    @Transactional
    public String gerarNovaSenhaTemporaria(Integer id) {
        Usuario usuario = buscarPorId(id);
        String novaSenha = gerarSenhaAleatoria(10);
        usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
        usuario.setPrimeiroAcesso(true);
        usuarioRepository.save(usuario);
        return novaSenha;
    }

    @Transactional
    public String recuperarSenha(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Informe o seu e-mail corporativo cadastrado.");
        }
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new IllegalArgumentException("Nenhum usuário cadastrado com o e-mail: " + email.trim()));

        if (Boolean.TRUE.equals(usuario.getBloqueado())) {
            throw new IllegalArgumentException("Este usuário encontra-se bloqueado pelo RH. Contate o suporte.");
        }
        if (Boolean.FALSE.equals(usuario.getAtivo())) {
            throw new IllegalArgumentException("Esta conta está inativa. Contate o RH.");
        }

        String novaSenhaTemp = gerarSenhaAleatoria(10);
        usuario.setSenhaHash(passwordEncoder.encode(novaSenhaTemp));
        usuario.setPrimeiroAcesso(true);
        usuarioRepository.save(usuario);
        return novaSenhaTemp;
    }

    @Transactional
    public void alterarSenha(Usuario usuario, String senhaAtual, String novaSenha, String confirmacao) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não identificado.");
        }
        if (senhaAtual == null || senhaAtual.isBlank()) {
            throw new IllegalArgumentException("Informe a sua senha atual.");
        }
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenhaHash())) {
            throw new IllegalArgumentException("A senha atual informada está incorreta.");
        }
        if (novaSenha == null || novaSenha.length() < 6) {
            throw new IllegalArgumentException("A nova senha deve possuir no mínimo 6 caracteres.");
        }
        if (!novaSenha.equals(confirmacao)) {
            throw new IllegalArgumentException("A confirmação da nova senha não confere.");
        }
        if (passwordEncoder.matches(novaSenha, usuario.getSenhaHash())) {
            throw new IllegalArgumentException("A nova senha não pode ser idêntica à senha atual.");
        }

        usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
        usuario.setPrimeiroAcesso(false);
        usuarioRepository.save(usuario);
    }

    private String gerarSenhaAleatoria(int tamanho) {
        StringBuilder sb = new StringBuilder(tamanho);
        for (int i = 0; i < tamanho; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}

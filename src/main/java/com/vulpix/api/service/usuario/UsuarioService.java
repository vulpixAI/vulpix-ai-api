package com.vulpix.api.service.usuario;

import com.vulpix.api.config.security.jwt.GerenciadorTokenJwt;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Usuario;
import com.vulpix.api.exception.exceptions.ConflitoException;
import com.vulpix.api.exception.exceptions.NaoAutorizadoException;
import com.vulpix.api.exception.exceptions.NaoEncontradoException;
import com.vulpix.api.repository.UsuarioRepository;
import com.vulpix.api.dto.Usuario.UsuarioLoginDto;
import com.vulpix.api.dto.Usuario.UsuarioMapper;
import com.vulpix.api.dto.Usuario.UsuarioTokenDto;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.utils.enums.StatusUsuario;
import com.vulpix.api.utils.helpers.EmpresaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GerenciadorTokenJwt gerenciadorTokenJwt;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;

    @Autowired
    private EmpresaHelper empresaHelper;

    public Usuario cadastrarUsuario(Usuario novoUsuario, String cnpj) {
        if (empresaHelper.isCnpjCadastrado(cnpj)) {
            throw new ConflitoException("Esse CNPJ já foi cadastrado.");
        }

        if (usuarioRepository.existsByEmail(novoUsuario.getEmail())) {
            throw new ConflitoException("Esse e-mail já foi cadastrado.");
        }

        novoUsuario.setSenha(passwordEncoder.encode(novoUsuario.getSenha()));
        return usuarioRepository.save(novoUsuario);
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(() -> new NaoEncontradoException("Usuário não encontrado pelo e-mail."));
    }

    public UsuarioTokenDto autenticarUsuario(UsuarioLoginDto usuarioLoginDto) {
        final UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(
                usuarioLoginDto.getEmail(), usuarioLoginDto.getSenha()
        );
        final Authentication authentication = this.authenticationManager.authenticate(credentials);

        Usuario usuarioAutenticado = buscarUsuarioPorEmail(usuarioLoginDto.getEmail());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String token = gerenciadorTokenJwt.generateToken(authentication);

        return UsuarioMapper.retornaUsuario(usuarioAutenticado, token);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarUsuarioPorId(UUID id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Usuário não encontrado pelo id."));
    }

    public Usuario atualizarUsuario(UUID id, Usuario usuarioAtualizado) {
        Usuario usuario = buscarUsuarioPorId(id);
        usuarioAtualizado.setId(usuario.getId());
        return usuarioRepository.save(usuarioAtualizado);
    }

    public boolean verificarSenhaAtual(String senhaHash, String senhaAtual) {
        Boolean isValidPassword = passwordEncoder.matches(senhaAtual, senhaHash);
        if (!isValidPassword) {
            throw new NaoAutorizadoException("A senha informada está incorreta.");
        }
        return true;
    }

    public void atualizarSenha(UUID id, String novaSenha) {
        Usuario usuario = buscarUsuarioPorId(id);
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    public boolean deletarUsuario(UUID id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        throw new NaoEncontradoException("Usuário não encontrado.");
    }

    public UUID retornaIdUsuarioLogado() {
        String usuario = usuarioAutenticadoUtil.getUsernameAutenticado();

        return usuarioRepository.findByEmail(usuario).get().getId();
    }

    public boolean atualizaStatus(Empresa empresa, StatusUsuario status) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmpresa(empresa);

        if (usuarioOpt.isEmpty()) {
            return false;
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setStatus(status);
        usuarioRepository.save(usuario);
        return true;
    }
}
package com.vulpix.api.Services.Usuario;

import com.vulpix.api.Config.Security.Jwt.GerenciadorTokenJwt;
import com.vulpix.api.Entity.Usuario;
import com.vulpix.api.Repository.UsuarioRepository;
import com.vulpix.api.Services.Usuario.Autenticacao.Dto.UsuarioLoginDto;
import com.vulpix.api.Services.Usuario.Autenticacao.Dto.UsuarioMapper;
import com.vulpix.api.Services.Usuario.Autenticacao.Dto.UsuarioTokenDto;
import com.vulpix.api.Services.Usuario.Autenticacao.UsuarioAutenticadoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public Usuario cadastrarUsuario(Usuario novoUsuario) {
        String senhaCriptografada = passwordEncoder.encode(novoUsuario.getSenha());
        novoUsuario.setSenha(senhaCriptografada);
        return usuarioRepository.save(novoUsuario);
    }

    public UsuarioTokenDto autenticarUsuario(UsuarioLoginDto usuarioLoginDto) {
        final UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(
                usuarioLoginDto.getEmail(), usuarioLoginDto.getSenha()
        );
        final Authentication authentication = this.authenticationManager.authenticate(credentials);

        Usuario usuarioAutenticado = usuarioRepository.findByEmail(usuarioLoginDto.getEmail())
                .orElseThrow(
                        () -> new ResponseStatusException(404, "Email do usuário não encontrado", null)
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String token = gerenciadorTokenJwt.generateToken(authentication);

        return UsuarioMapper.retornaUsuario(usuarioAutenticado, token);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarUsuarioPorId(UUID id) {
        return usuarioRepository.findById(id);
    }


    public Optional<Usuario> atualizarUsuario(
            UUID id, Usuario usuarioAtualizado) {
        if (usuarioRepository.existsById(id)) {
            usuarioAtualizado.setId(id);
            Usuario usuarioSalvo = usuarioRepository.save(usuarioAtualizado);
            return Optional.of(usuarioSalvo);
        }
        return Optional.empty();
    }

    public boolean deletarUsuario(UUID id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public UUID retornaIdUsuarioLogado() {
        String usuario = usuarioAutenticadoUtil.getUsernameAutenticado();

        return usuarioRepository.findByEmail(usuario).get().getId();
    }
}

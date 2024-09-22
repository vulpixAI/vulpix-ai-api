package com.vulpix.api.services;

import com.vulpix.api.dto.GetUsuarioDto;
import com.vulpix.api.entity.Usuario;
import com.vulpix.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Optional<GetUsuarioDto> cadastrarUsuario(
            Usuario novoUsuario) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(novoUsuario.getEmail());
        if (usuarioExistente.isPresent()) {
            return Optional.empty();
        }
        novoUsuario.setId(null);
        novoUsuario.setCreated_at(LocalDateTime.now());
        novoUsuario.setAtivo(true);
        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        return Optional.of(montaRetornoUsuario(usuarioSalvo));
    }

    public Optional<GetUsuarioDto> autenticarUsuario(
            String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailAndSenha(email, senha);
        return usuarioOpt.map(this::montaRetornoUsuario);
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

    public GetUsuarioDto montaRetornoUsuario(Usuario usuario) {
        GetUsuarioDto usuarioRetorno = new GetUsuarioDto(
                usuario.getId(),
                usuario.getNome(),
                usuario.getSobrenome(),
                usuario.getEmail(),
                usuario.isAtivo(),
                usuario.getTelefone(),
                usuario.getCreated_at(),
                usuario.getUpdated_at()
        );
        return usuarioRetorno;
    }
}

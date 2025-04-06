package com.vulpix.api.service.usuario.autenticacao;

import com.vulpix.api.dto.usuario.UsuarioDetalhesDto;
import com.vulpix.api.entity.Usuario;
import com.vulpix.api.exception.exceptions.NaoEncontradoException;
import com.vulpix.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(username);

        if (usuarioOpt.isEmpty()) {
            throw new NaoEncontradoException(String.format("Usuário: %s não encontrado.", username));
        }

        return new UsuarioDetalhesDto(usuarioOpt.get());
    }
}
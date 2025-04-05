package com.vulpix.api.utils.helpers;

import com.vulpix.api.entity.Usuario;
import com.vulpix.api.exception.exceptions.NaoEncontradoException;
import com.vulpix.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsuarioHelper {
    @Autowired
    UsuarioRepository usuarioRepository;

    public Usuario buscaUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(() -> new NaoEncontradoException("Usuário não encontrado pelo email"));
    }

    public void cadastrarSecretKey(String secretKey, Usuario usuario){
        usuario.setSecretKey(secretKey);
        usuarioRepository.save(usuario);
    }

    public void desabilitarAutenticacao(Usuario usuario){
        usuario.setSecretKey(null);
        usuarioRepository.save(usuario);
    }

    public void marcarDispositivoComoConfiavel(Usuario usuario, String dispositivoCode) {
        usuario.setDispositivoConfiavel(dispositivoCode);
        usuario.setDispositivoExpiraEm(LocalDateTime.now().plusDays(30));

        usuarioRepository.save(usuario);
    }
}

package com.vulpix.api.dto.Usuario;

import com.vulpix.api.entity.Usuario;
import com.vulpix.api.services.Usuario.Autenticacao.dto.UsuarioTokenDto;

public class UsuarioMapper {
    public static UsuarioTokenDto of(Usuario usuario, String token) {
        UsuarioTokenDto usuarioTokenDto = new UsuarioTokenDto();

        usuarioTokenDto.setUserId(usuario.getId());
        usuarioTokenDto.setEmail(usuario.getEmail());
        usuarioTokenDto.setNome(usuario.getNome());
        usuarioTokenDto.setToken(token);

        return usuarioTokenDto;
    }
}

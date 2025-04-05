package com.vulpix.api.dto.usuario;

import com.vulpix.api.dto.autenticacao.UsuarioTokenDto;
import com.vulpix.api.entity.Usuario;

public class UsuarioMapper {
    public static UsuarioTokenDto retornaUsuario(Usuario usuario, String token){
        if (usuario == null || token == null) return null;

        UsuarioTokenDto usuarioTokenDto = UsuarioTokenDto.builder()
                .token(token)
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .userId(usuario.getId())
                .status(usuario.getStatus())
                .build();
        return usuarioTokenDto;
    }
}

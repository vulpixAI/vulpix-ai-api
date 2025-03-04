package com.vulpix.api.service.usuario.autenticacao.dto;

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

package com.vulpix.api.dto.usuario;

import com.vulpix.api.dto.autenticacao.UsuarioTokenDto;
import com.vulpix.api.entity.Usuario;

public class UsuarioMapper {
    public static UsuarioTokenDto retornaUsuario(Usuario usuario, String token, String secretKey){
        if (usuario == null || token == null) return null;

        return new UsuarioTokenDto(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                token,
                usuario.getStatus(),
                secretKey);
    }
}

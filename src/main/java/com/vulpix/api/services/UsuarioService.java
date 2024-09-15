package com.vulpix.api.services;

import com.vulpix.api.dto.GetUsuarioDto;
import com.vulpix.api.entity.Usuario;

public class UsuarioService {
    public GetUsuarioDto montaRetornoUsuario(Usuario usuario){
        GetUsuarioDto usuarioRetorno = new GetUsuarioDto(
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

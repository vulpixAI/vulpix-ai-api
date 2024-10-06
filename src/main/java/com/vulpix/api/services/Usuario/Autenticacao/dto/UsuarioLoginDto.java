package com.vulpix.api.services.Usuario.Autenticacao.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioLoginDto {
    private String email;
    private String senha;
}

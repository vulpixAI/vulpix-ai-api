package com.vulpix.api.Services.Usuario.Autenticacao.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioLoginDto {
    private String email;
    private String senha;
}

package com.vulpix.api.service.usuario.autenticacao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioLoginDto {
    private String email;
    private String senha;
}

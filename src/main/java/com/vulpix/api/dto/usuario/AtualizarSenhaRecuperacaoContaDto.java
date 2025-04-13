package com.vulpix.api.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AtualizarSenhaRecuperacaoContaDto {
    @NotBlank
    private String novaSenha;
}
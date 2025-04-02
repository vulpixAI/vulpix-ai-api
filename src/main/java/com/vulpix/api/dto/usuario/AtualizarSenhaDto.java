package com.vulpix.api.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AtualizarSenhaDto {
    @NotBlank
    private String senhaAtual;
    @NotBlank
    private String novaSenha;

}
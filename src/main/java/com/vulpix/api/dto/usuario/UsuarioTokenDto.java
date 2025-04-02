package com.vulpix.api.dto.usuario;

import com.vulpix.api.utils.enums.StatusUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioTokenDto {
    private UUID userId;
    private String nome;
    private String email;
    private String token;
    private StatusUsuario status;
}

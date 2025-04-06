package com.vulpix.api.dto.autenticacao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vulpix.api.utils.enums.StatusUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
public class UsuarioTokenDto extends LoginResponse{
    private UUID userId;
    private String nome;
    private String token;
    private StatusUsuario status;

    public UsuarioTokenDto(UUID userId, String nome, String email, String token, StatusUsuario statusUsuario, String secretKey) {
        super(false, email, secretKey);
        this.userId = userId;
        this.nome = nome;
        this.token = token;
        this.status = statusUsuario;
    }
}

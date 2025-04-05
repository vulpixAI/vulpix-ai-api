package com.vulpix.api.dto.autenticacao;

import com.vulpix.api.utils.enums.StatusUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
public class UsuarioTokenDto extends LoginResponse{
    private UUID userId;
    private String nome;
    private String email;
    private String token;
    private StatusUsuario status;


    public UsuarioTokenDto(UUID userId, String nome, String email, String token, StatusUsuario statusUsuario) {
        super(false, email);
        this.userId = userId;
        this.nome = nome;
        this.email = email;
        this.token = token;
        this.status = statusUsuario;
    }
}

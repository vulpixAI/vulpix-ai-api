package com.vulpix.api.dto.autenticacao;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public abstract class LoginResponse {
    private boolean mfaRequerido;
    private String email;
    private String secretKey;

    public LoginResponse(boolean mfaRequerido, String email, String secretKey) {
        this.mfaRequerido = mfaRequerido;
        this.email = email;
        this.secretKey = secretKey;
    }

    public boolean isMfaRequerido() {
        return mfaRequerido;
    }
}

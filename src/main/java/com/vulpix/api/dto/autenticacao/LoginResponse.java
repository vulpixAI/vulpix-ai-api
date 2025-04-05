package com.vulpix.api.dto.autenticacao;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public abstract class LoginResponse {
    private boolean mfaRequerido;
    private String email;

    public LoginResponse(boolean mfaRequerido, String email) {
        this.mfaRequerido = mfaRequerido;
        this.email = email;
    }

    public boolean isMfaRequerido() {
        return mfaRequerido;
    }
}

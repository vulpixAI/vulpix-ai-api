package com.vulpix.api.dto.autenticacao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
public class MfaRequiredResponse extends LoginResponse {
    private String status = "MFA_REQUIRED";
    private String email;
    public MfaRequiredResponse(String email) {
        super(true, email);
        this.email = email;
    }

}

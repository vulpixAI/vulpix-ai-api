package com.vulpix.api.dto.autenticacao;

import lombok.Data;

@Data
public class MfaLoginDto {
    private String email;
    private String otp;
    private String secretKey;
    private String dispositivoCode;
}
